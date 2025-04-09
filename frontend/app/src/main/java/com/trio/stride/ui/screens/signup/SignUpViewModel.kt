package com.trio.stride.ui.screens.signup

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.trio.stride.base.BaseViewModel
import com.trio.stride.base.Resource
import com.trio.stride.domain.model.AuthInfo
import com.trio.stride.domain.usecase.auth.LoginWithGoogleUseCase
import com.trio.stride.domain.usecase.identity.SignUpUseCase
import com.trio.stride.domain.usecase.profile.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val getUseCase: GetUserUseCase,
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
        setState { SignUpViewState.Loading }

        viewModelScope.launch {
            val result = signUpUseCase(email, password)
            result
                .onSuccess { data ->
                    setState { SignUpViewState.Success(data) }
                }
                .onFailure {
                    setState { SignUpViewState.Error(it.message ?: "An error occurred") }
                }
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            loginWithGoogleUseCase.invoke(idToken).collectLatest { response ->
                when (response) {
                    is Resource.Loading -> setState { SignUpViewState.Loading }
                    is Resource.Success -> {
                        when (response.data) {
                            is AuthInfo.WithToken -> {
                                setState { SignUpViewState.WithGoogleSuccess }
                                getUser()
                            }

                            is AuthInfo.WithUserIdentity ->
                                setState { SignUpViewState.Success(response.data.userIdentityId) }

                            null -> setState { SignUpViewState.Error("Can't get information") }
                        }
                    }

                    is Resource.Error -> setState { SignUpViewState.Error(response.error.message.toString()) }
                }
            }
        }
    }

    fun getUser() {
        viewModelScope.launch {
            getUseCase.invoke().collectLatest { response ->
                when (response) {
                    is Resource.Success -> Log.i("USER INFO", response.data.toString())
                    else -> {}
                }
            }
        }
    }
}
