package com.trio.stride.ui.screens.verifyOtp

import com.trio.stride.domain.viewstate.IViewState

sealed class VerifyOtpViewState: IViewState {
        object Idle : VerifyOtpViewState()
        object Loading : VerifyOtpViewState()
        data class Success(val message: String) : VerifyOtpViewState()
        data class Error(val message: String) : VerifyOtpViewState()
        data class Countdown(val second: Int) : VerifyOtpViewState()

}