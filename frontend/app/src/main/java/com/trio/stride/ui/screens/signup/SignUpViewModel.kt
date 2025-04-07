package com.trio.stride.ui.screens.signup

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.trio.stride.base.BaseViewModel
import com.trio.stride.domain.usecase.identity.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

const val tag="SignUpTag"

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
) : BaseViewModel<SignUpViewState>() {
    override fun createInitialState(): SignUpViewState {
        return SignUpViewState.Idle
    }

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var loading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var success by mutableStateOf(false)

    fun signUp(email: String, password: String) {
        // Trigger loading state
        setState { SignUpViewState.Loading }

        viewModelScope.launch {
            val result = signUpUseCase(email, password)
            result
                .onSuccess { data ->
                    setState { SignUpViewState.Success(data) }
                    Log.d(tag, data)
                }
                .onFailure {
                    setState { SignUpViewState.Error(it.message ?: "An error occurred") }
                }
        }
    }
}
