package com.trio.stride.ui.screens.login

import androidx.lifecycle.viewModelScope
import com.trio.stride.base.BaseViewModel
import com.trio.stride.base.NotFoundException
import com.trio.stride.base.UnauthorizedException
import com.trio.stride.domain.usecase.auth.LoginUseCase
import com.trio.stride.domain.viewstate.IViewState
import com.trio.stride.ui.utils.isValidEmail
import com.trio.stride.ui.utils.isValidPassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : BaseViewModel<LoginViewModel.LoginViewState>() {
    override fun createInitialState(): LoginViewState {
        return LoginViewState()
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            if (isValidEmail(email) && isValidPassword(password)) {
                loginUseCase.invoke(email = email, password = password).collectLatest { response ->
                    when (response) {
                        is Resource.Loading -> setState { currentState.copy(state = LoginState.LOADING) }
                        is Resource.Success -> {
                            setState { currentState.copy(message = "Success", state = LoginState.SUCCESS) }
                        }
                        is Resource.Error -> {
                            if (response.error is UnauthorizedException)
                                setState { currentState.copy(message = "Unauthorized User", state = LoginState.UNAUTHORIZED) }
                            else if (response.error is NotFoundException)
                                setState { currentState.copy(message = "Invalid email or password", state = LoginState.ERROR) }
                            else
                                setState {
                                    currentState.copy(
                                        message = response.error.message.toString(),
                                        state = LoginState.ERROR
                                    )
                                }
                        }
                    }
                }
            } else {
                setState { currentState.copy(message = "invalid email or password") }
            }

        }
    }

    data class LoginViewState(
        val state: LoginState = LoginState.NONE,
        val message: String = ""
    ) : IViewState

    enum class LoginState { LOADING, SUCCESS, ERROR, NONE, UNAUTHORIZED }
}