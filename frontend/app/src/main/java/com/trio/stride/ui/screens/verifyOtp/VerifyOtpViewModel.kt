package com.trio.stride.ui.screens.verifyOtp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.trio.stride.base.BaseViewModel
import com.trio.stride.domain.usecase.identity.SendOtpUseCase
import com.trio.stride.domain.usecase.identity.VerifyOtpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyOtpViewModel @Inject constructor(
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val sendOtpUseCase: SendOtpUseCase
) : BaseViewModel<VerifyOtpViewState>() {
    override fun createInitialState(): VerifyOtpViewState {
        return VerifyOtpViewState.Idle
    }

    fun verifyOtp(otpCode: String, userIdentity: String) {
        setState { VerifyOtpViewState.Loading }

        viewModelScope.launch {
            val result = verifyOtpUseCase(userIdentity, otpCode)
            result
                .onSuccess { data ->
                    setState { VerifyOtpViewState.Success(data) }
                }
                .onFailure {
                    setState { VerifyOtpViewState.Error(it.message ?: "An error occurred") }
                }
        }
    }

    fun sendOtp(userIdentity: String) {
        setState { VerifyOtpViewState.Loading }
        viewModelScope.launch {
            val result = sendOtpUseCase(userIdentity)
            result
                .onSuccess {
                    startCountdown()
                }
                .onFailure {
                    setState { VerifyOtpViewState.Error(it.message ?: "An error occurred") }
                }
        }
    }

    fun startCountdown (seconds: Int = 60) {
        viewModelScope.launch {
            for (time in seconds downTo 0) {
                setState { VerifyOtpViewState.Countdown(time) }
                delay(1000L)
            }
            setState { VerifyOtpViewState.Idle }
        }
    }
}
