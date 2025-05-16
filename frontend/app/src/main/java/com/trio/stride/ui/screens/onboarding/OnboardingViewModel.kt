package com.trio.stride.ui.screens.onboarding

import androidx.lifecycle.viewModelScope
import com.trio.stride.base.BaseViewModel
import com.trio.stride.base.Resource
import com.trio.stride.data.remote.dto.UpdateUserRequestDto
import com.trio.stride.domain.usecase.profile.GetUserUseCase
import com.trio.stride.domain.usecase.profile.SyncUserUseCase
import com.trio.stride.domain.usecase.profile.UpdateUserUseCase
import com.trio.stride.domain.viewstate.IViewState
import com.trio.stride.ui.utils.toDateString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val updateUserUseCase: UpdateUserUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val syncUserUseCase: SyncUserUseCase
) : BaseViewModel<OnboardingViewModel.ScreenState>() {
    override fun createInitialState(): ScreenState = ScreenState()

    init {
        viewModelScope.launch {
            getUserUseCase.invoke().collectLatest { response ->
                if (response is Resource.Success) {
                    setState {
                        currentState.copy(
                            userInitInfo = currentState.userInitInfo.copy(
                                name = response.data.name
                            )
                        )
                    }

                    moveToInfo()
                }
            }
        }
    }

    fun updateName(value: String) {
        val viewState = currentState.viewState
        if (viewState is OnboardingViewState.Info) {
            setState {
                currentState.copy(
                    userInitInfo = currentState.userInitInfo.copy(name = value),
                    viewState = viewState.copy(name = value)
                )
            }
        }
    }

    fun updateDob(value: String) {
        val viewState = currentState.viewState
        if (viewState is OnboardingViewState.Info) {
            setState {
                currentState.copy(
                    userInitInfo = currentState.userInitInfo.copy(dob = value),
                    viewState = viewState.copy(dob = value)
                )
            }
        }
    }

    fun updateGender(value: Boolean) {
        val viewState = currentState.viewState
        if (viewState is OnboardingViewState.Info) {
            setState {
                currentState.copy(
                    userInitInfo = currentState.userInitInfo.copy(male = value),
                    viewState = viewState.copy(male = value)
                )
            }
        }
    }

    fun updateHeight(value: Int) {
        val viewState = currentState.viewState
        if (viewState is OnboardingViewState.Info) {
            setState {
                currentState.copy(
                    userInitInfo = currentState.userInitInfo.copy(height = value),
                    viewState = viewState.copy(height = value)
                )
            }
        }
    }

    fun updateWeight(value: Int) {
        val viewState = currentState.viewState
        if (viewState is OnboardingViewState.Info) {
            setState {
                currentState.copy(
                    userInitInfo = currentState.userInitInfo.copy(weight = value),
                    viewState = viewState.copy(weight = value)
                )
            }
        }
    }

    fun moveToInfo() {
        setState { currentState.copy(viewState = OnboardingViewState.Info()) }

        val viewState = currentState.viewState
        if (viewState is OnboardingViewState.Info) {
            setState {
                currentState.copy(
                    viewState = viewState.copy(
                        name = currentState.userInitInfo.name,
                    )
                )
            }
        }
    }

    fun success() {
        setState { currentState.copy(viewState = OnboardingViewState.Success) }
    }

    fun updateUser() {
        val viewState = currentState.viewState
        if (viewState is OnboardingViewState.Info) {

            viewModelScope.launch {
                updateUserUseCase.invoke(currentState.userInitInfo).collectLatest { response ->
                    when (response) {
                        is Resource.Loading -> setState {
                            currentState.copy(
                                viewState = viewState.copy(
                                    isLoading = true,
                                    isError = false,
                                    errorMessage = null
                                )
                            )
                        }

                        is Resource.Success -> {
                            setState { currentState.copy(viewState = OnboardingViewState.Success) }
                            syncUserUseCase.invoke()
                        }

                        is Resource.Error -> setState {
                            currentState.copy(
                                viewState = viewState.copy(
                                    isError = true,
                                    errorMessage = response.error.message.toString()
                                )
                            )
                        }
                    }
                }

            }
        }
    }

    data class ScreenState(
        val viewState: OnboardingViewState = OnboardingViewState.Info(),
        val userInitInfo: UpdateUserRequestDto = UpdateUserRequestDto(
            name = "",
            dob = LocalDateTime.now().minusYears(20).toDateString(),
            height = 160,
            weight = 50,
            male = true
        )
    ) : IViewState

    sealed class OnboardingViewState : IViewState {
        data object Success : OnboardingViewState()
        data object Welcome : OnboardingViewState()
        data class Info(
            val isLoading: Boolean = false,
            val isError: Boolean = false,
            val errorMessage: String? = null,
            val name: String = "",
            val dob: String = LocalDateTime.now().minusYears(20).toDateString(),
            val male: Boolean = true,
            val height: Int = 160,
            val weight: Int = 50
        ) : OnboardingViewState()
    }
}