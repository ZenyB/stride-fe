package com.trio.stride.ui.screens.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.trio.stride.base.BaseViewModel
import com.trio.stride.base.Resource
import com.trio.stride.base.SyncLocalDataFailed
import com.trio.stride.data.datastoremanager.TokenManager
import com.trio.stride.data.remote.dto.UpdateUserRequestDto
import com.trio.stride.data.repositoryimpl.RecordRepository
import com.trio.stride.data.service.RecordService
import com.trio.stride.domain.model.UserInfo
import com.trio.stride.domain.usecase.auth.LogoutUseCase
import com.trio.stride.domain.usecase.file.UploadFileUseCase
import com.trio.stride.domain.usecase.profile.GetUserUseCase
import com.trio.stride.domain.usecase.profile.SyncUserUseCase
import com.trio.stride.domain.usecase.profile.UpdateUserUseCase
import com.trio.stride.domain.viewstate.IViewState
import com.trio.stride.ui.utils.isValidBirthDay
import com.trio.stride.ui.utils.toBoolGender
import com.trio.stride.ui.utils.toDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val uploadFileUseCase: UploadFileUseCase,
    private val syncUserUseCase: SyncUserUseCase,
    private val recordRepository: RecordRepository,
    private val logoutUseCase: LogoutUseCase,
    private val tokenManager: TokenManager
) : BaseViewModel<ProfileViewModel.ViewState>() {

    override fun createInitialState(): ViewState = ViewState()

    init {
        getUserInfo()
    }

    private fun setError(errorField: ErrorField) {
        val newErrorFields = currentState.errorFields.toMutableMap()
        newErrorFields[errorField] = true
        setState {
            currentState.copy(
                errorFields = newErrorFields.toMap(),
            )
        }
    }

    private fun getUserInfo() {
        viewModelScope.launch {
            getUserUseCase.invoke().collectLatest { response ->
                when (response) {
                    is Resource.Loading -> setState { currentState.copy(isLoading = true) }
                    is Resource.Success -> {
                        setState { currentState.copy(userInfo = response.data, isLoading = false) }
                    }

                    is Resource.Error -> {
                        setState {
                            currentState.copy(
                                isLoading = false,
                                isError = true,
                                errorMessage = response.error.message.toString()
                            )
                        }
                    }
                }
            }
        }
    }

    fun updateProfile(context: Context, uri: Uri?, onSuccess: () -> Unit = {}) {
        clearError()
        if (!validateUserInfo())
            return
        uri?.let {
            setState { currentState.copy(isLoading = true) }
            viewModelScope.launch {
                val avaUrl = coroutineScope {
                    async {
                        var result = ""
                        uploadFileUseCase.invoke(uri, context).first { response ->
                            when (response) {
                                is Resource.Success -> {
                                    result = response.data
                                    true
                                }

                                is Resource.Error -> {
                                    setState {
                                        currentState.copy(
                                            isLoading = false,
                                            isError = true,
                                            errorMessage = response.error.message.toString()
                                        )
                                    }
                                    true
                                }

                                is Resource.Loading -> false
                            }
                        }
                        result
                    }
                }.await()
                val request = UpdateUserRequestDto(
                    name = currentState.userInfo.name,
                    city = currentState.userInfo.city,
                    ava = avaUrl,
                    dob = currentState.userInfo.dob,
                    height = currentState.userInfo.height,
                    weight = currentState.userInfo.weight,
                    male = currentState.userInfo.male,
                    maxHeartRate = currentState.userInfo.maxHeartRate,
                )
                updateUserUseCase.invoke(
                    request = request
                ).collectLatest { response ->
                    when (response) {
                        is Resource.Loading -> setState {
                            currentState.copy(
                                isLoading = true,
                                isError = false,
                                errorMessage = null
                            )
                        }

                        is Resource.Success -> {
                            setState {
                                currentState.copy(
                                    isUpdateSuccess = true,
                                    isLoading = false,
                                    userInfo = userInfo.copy(ava = avaUrl)
                                )
                            }
                            onSuccess()
                        }

                        is Resource.Error -> {
                            if (response.error is SyncLocalDataFailed) {
                                setState {
                                    currentState.copy(isNotSync = true, isLoading = false)
                                }
                            } else
                                setState {
                                    currentState.copy(
                                        isLoading = false,
                                        isError = true,
                                        errorMessage = response.error.message.toString()
                                    )
                                }
                        }
                    }
                }
            }
        }

        if (uri == null) {
            viewModelScope.launch {
                val request = UpdateUserRequestDto(
                    name = currentState.userInfo.name,
                    city = currentState.userInfo.city,
                    ava = currentState.userInfo.ava,
                    dob = currentState.userInfo.dob,
                    height = currentState.userInfo.height,
                    weight = currentState.userInfo.weight,
                    male = currentState.userInfo.male,
                    maxHeartRate = currentState.userInfo.maxHeartRate,
                )
                updateUserUseCase.invoke(
                    request = request
                ).collectLatest { response ->
                    when (response) {
                        is Resource.Loading -> setState {
                            currentState.copy(
                                isLoading = true,
                                isError = false,
                                errorMessage = null
                            )
                        }

                        is Resource.Success -> {
                            setState {
                                currentState.copy(
                                    isUpdateSuccess = true,
                                    isLoading = false
                                )
                            }
                            onSuccess()
                        }

                        is Resource.Error -> {
                            if (response.error is SyncLocalDataFailed) {
                                setState {
                                    currentState.copy(isNotSync = true, isLoading = false)
                                }
                            } else
                                setState {
                                    currentState.copy(
                                        isLoading = false,
                                        isError = true,
                                        errorMessage = response.error.message.toString()
                                    )
                                }
                        }
                    }
                }
            }
        }
    }

    private fun validateUserInfo(): Boolean {
        val isNameValid = currentState.userInfo.name.isNotBlank()
        val isDobValid = currentState.userInfo.dob.toDate().isValidBirthDay()
        val isMaxHeartRateValid = currentState.userInfo.maxHeartRate in 20..260
        val isHeightValid = currentState.userInfo.height > 10
        val isWeightValid = currentState.userInfo.weight > 10
        val isShoesWeightValid = currentState.userInfo.equipmentsWeight.shoes >= 0
        val isBagWeightValid = currentState.userInfo.equipmentsWeight.bag >= 0

        if (!isNameValid)
            setError(ErrorField.NAME)

        if (!isDobValid)
            setError(ErrorField.DOB)

        if (!isMaxHeartRateValid)
            setError(ErrorField.HEART_RATE)

        if (!isHeightValid)
            setError(ErrorField.HEIGHT)

        if (!isWeightValid)
            setError(ErrorField.WEIGHT)

        if (!isShoesWeightValid)
            setError(ErrorField.SHOES)

        if (!isBagWeightValid)
            setError(ErrorField.BAG)

        return isNameValid && isDobValid && isMaxHeartRateValid && isHeightValid
                && isWeightValid && isShoesWeightValid && isBagWeightValid
    }

    fun logout(context: Context) {
        val startIntent = Intent(context, RecordService::class.java).apply {
            action = RecordService.STOP_RECORDING
        }
        context.startService(startIntent)

        recordRepository.end()

        viewModelScope.launch {
            setState { currentState.copy(loggingOut = true) }

            try {
                logoutUseCase.invoke().collectLatest { response ->
                    when (response) {
                        is Resource.Success -> {
                            setState { currentState.copy(logoutSuccess = true) }
                        }

                        is Resource.Error -> {
                            setState { currentState.copy(logoutError = true) }
                        }

                        is Resource.Loading -> {}
                    }

                }
            } catch (e: Exception) {
                setState { currentState.copy(logoutError = true) }
            } finally {
                setState { currentState.copy(loggingOut = false) }
            }
        }
    }

    fun updateName(value: String) {
        setState { currentState.copy(userInfo = currentState.userInfo.copy(name = value)) }
    }

    fun updateCity(value: String) {
        setState { currentState.copy(userInfo = currentState.userInfo.copy(city = value)) }
    }

    fun updateDob(value: String) {
        setState { currentState.copy(userInfo = currentState.userInfo.copy(dob = value)) }
    }

    fun updateGender(value: String) {
        val gender = value.toBoolGender()
        setState { currentState.copy(userInfo = currentState.userInfo.copy(male = gender)) }
    }

    fun updateMaxHeartRate(value: Int) {
        setState { currentState.copy(userInfo = currentState.userInfo.copy(maxHeartRate = value)) }
    }

    fun updateHeight(value: Int) {
        setState { currentState.copy(userInfo = currentState.userInfo.copy(height = value)) }
    }

    fun updateWeight(value: Int) {
        setState { currentState.copy(userInfo = currentState.userInfo.copy(weight = value)) }
    }

    fun updateShoesWeight(value: Int) {
        setState {
            currentState.copy(
                userInfo = currentState.userInfo.copy(
                    equipmentsWeight = currentState.userInfo.equipmentsWeight.copy(
                        shoes = value
                    )
                )
            )
        }
    }

    fun updateBagWeight(value: Int) {
        setState {
            currentState.copy(
                userInfo = currentState.userInfo.copy(
                    equipmentsWeight = currentState.userInfo.equipmentsWeight.copy(
                        bag = value
                    )
                )
            )
        }
    }

    fun clearError() {
        setState {
            currentState.copy(
                isError = false, errorMessage = null, errorFields = mapOf(
                    ErrorField.NAME to false,
                    ErrorField.DOB to false,
                    ErrorField.HEART_RATE to false,
                    ErrorField.HEIGHT to false,
                    ErrorField.WEIGHT to false
                )
            )
        }
    }

    fun changeToEdit() {
        viewModelScope.launch {
            setState { currentState.copy(isLoading = true) }
            async { syncUserUseCase.invoke() }.await()
            setState {
                currentState.copy(
                    isEditProfile = true,
                    isLoading = false,
                    isUpdateSuccess = false
                )
            }
        }
    }

    fun changeToDefault() {
        setState { currentState.copy(isEditProfile = false) }
    }

    fun ignoreIsNotSync() {
        setState { currentState.copy(isNotSync = false) }
    }

    data class ViewState(
        val isEditProfile: Boolean = false,
        val isLoading: Boolean = true,
        val loggingOut: Boolean = false,
        val logoutSuccess: Boolean = false,
        val logoutError: Boolean = false,
        val isUploadImage: Boolean = false,
        val isError: Boolean = false,
        val isNotSync: Boolean = false,
        val errorMessage: String? = null,
        val errorFields: Map<ErrorField, Boolean> = mapOf(
            ErrorField.NAME to false,
            ErrorField.DOB to false,
            ErrorField.HEART_RATE to false,
            ErrorField.HEIGHT to false,
            ErrorField.WEIGHT to false,
            ErrorField.SHOES to false,
            ErrorField.BAG to false
        ),
        val isUpdateSuccess: Boolean = false,
        val userInfo: UserInfo = UserInfo(),
    ) : IViewState

    enum class ErrorField { NAME, DOB, HEART_RATE, HEIGHT, WEIGHT, SHOES, BAG }
}