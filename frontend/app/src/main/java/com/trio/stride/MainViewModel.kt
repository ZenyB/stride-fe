package com.trio.stride

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trio.stride.base.Resource
import com.trio.stride.data.datastoremanager.TokenManager
import com.trio.stride.domain.usecase.profile.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val getUserUseCase: GetUserUseCase,
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState.UNKNOWN)
    val authState: StateFlow<AuthState> = _authState

    init {
        viewModelScope.launch {
            tokenManager.getAccessToken().collect { token ->
                if (!token.isNullOrEmpty()) {
                    getUser()
                } else _authState.value = AuthState.UNAUTHORIZED
            }
        }
    }

    private fun getUser() {
        viewModelScope.launch {
            getUserUseCase.invoke().collectLatest { response ->
                if (response is Resource.Success) {
                    _authState.value =
                        if (response.data.dob.isBlank()) AuthState.AUTHORIZED_NOT_INITIALIZED else AuthState.AUTHORIZED
                }
            }
        }
    }

    enum class AuthState { UNKNOWN, AUTHORIZED, AUTHORIZED_NOT_INITIALIZED, UNAUTHORIZED }
}