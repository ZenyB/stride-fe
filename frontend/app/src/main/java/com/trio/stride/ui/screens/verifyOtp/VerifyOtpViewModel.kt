package com.trio.stride.ui.screens.verifyOtp

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.trio.stride.base.BaseViewModel
import com.trio.stride.domain.usecase.identity.SignUpUseCase
import com.trio.stride.domain.usecase.identity.VerifyOtpUseCase
import com.trio.stride.ui.screens.signup.SignUpViewState
import com.trio.stride.ui.screens.signup.tag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyOtpViewModel @Inject constructor(
    private val verifyOtpUseCase: VerifyOtpUseCase
) : BaseViewModel<VerifyOtpViewState>() {
    override fun createInitialState(): VerifyOtpViewState {
        return VerifyOtpViewState.Idle
    }

    var otpCode by mutableStateOf("")
    var loading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var success by mutableStateOf(false)

    fun verifyOtp(otpCode: String, userIdentity: String) {
        // Trigger loading state
        setState { VerifyOtpViewState.Loading }

        viewModelScope.launch {
            val result = verifyOtpUseCase(userIdentity, otpCode)
            result
                .onSuccess { data ->
                    setState { VerifyOtpViewState.Success(data) }
                    Log.d(tag, data)
                }
                .onFailure {
                    setState { VerifyOtpViewState.Error(it.message ?: "An error occurred") }
                }
        }
    }
}
