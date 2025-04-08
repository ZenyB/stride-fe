package com.trio.stride.ui.screens.forgotpassword

import androidx.lifecycle.viewModelScope
import com.trio.stride.base.BaseViewModel
import com.trio.stride.domain.usecase.identity.ChangePasswordUseCase
import com.trio.stride.domain.usecase.identity.ResetPasswordVerifyUseCase
import com.trio.stride.domain.usecase.identity.SendOtpResetPasswordUseCase
import com.trio.stride.domain.viewstate.IViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val sendOtpResetPasswordUseCase: SendOtpResetPasswordUseCase,
    private val resetPasswordVerifyUseCase: ResetPasswordVerifyUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase
) : BaseViewModel<ForgotPasswordViewModel.ForgotPasswordViewState>() {
    override fun createInitialState(): ForgotPasswordViewState {
        return ForgotPasswordViewState()
    }

    fun sendOtp(username: String) {
        setState { currentState.copy(isLoading = true) }

        viewModelScope.launch {
            val result = sendOtpResetPasswordUseCase.invoke(username)
            result
                .onSuccess { data ->
                    setState {
                        currentState.copy(
                            currentProgress = Progress.VERIFY_OTP,
                        )
                    }
                }
                .onFailure {
                    setState {
                        currentState.copy(
                            errorMessage = it.message
                        )
                    }
                }
        }
    }

    fun verifyOtp(username: String, otpCode: String) {
        setState { currentState.copy(isLoading = true) }

        viewModelScope.launch {
            val result = resetPasswordVerifyUseCase.invoke(username, otpCode)
            result
                .onSuccess { data ->
                    setState {
                        currentState.copy(
                            currentProgress = Progress.CHANGE_PASSWORD,
                            resetPasswordTokenId = data
                        )
                    }
                }
                .onFailure {
                    setState {
                        currentState.copy(
                            errorMessage = it.message
                        )
                    }
                }
        }
    }

    fun changePassword(username: String, password: String) {
        setState { currentState.copy(isLoading = true) }

        viewModelScope.launch {
            val result =
                changePasswordUseCase.invoke(currentState.resetPasswordTokenId, username, password)
            result
                .onSuccess {
                    setState {
                        currentState.copy(
                            currentProgress = Progress.CHANGE_PASSWORD
                        )
                    }
                }
                .onFailure {
                    setState {
                        currentState.copy(
                            errorMessage = it.message
                        )
                    }
                }
        }
    }

    data class ForgotPasswordViewState(
        val isLoading: Boolean = false,
        val resetPasswordTokenId: String = "",
        val currentProgress: Progress = Progress.SEND_OTP,
        val errorMessage: String? = null,
    ) : IViewState

    enum class Progress { SEND_OTP, VERIFY_OTP, CHANGE_PASSWORD, SUCCESS }
}