package com.trio.stride

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trio.stride.data.datastoremanager.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState.UNKNOWN)
    val authState: StateFlow<AuthState> = _authState

    init {
        viewModelScope.launch {
            tokenManager.getAccessToken().collect { token ->
                _authState.value =
                    if (!token.isNullOrEmpty()) AuthState.AUTHORIZED else AuthState.UNAUTHORIZED
            }
        }
    }

    enum class AuthState { UNKNOWN, AUTHORIZED, UNAUTHORIZED }
}