package com.trio.stride

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trio.stride.base.Resource
import com.trio.stride.data.datastoremanager.TokenManager
import com.trio.stride.domain.usecase.fcmnotification.EnqueueDeleteFCMTokenWorkerUseCase
import com.trio.stride.domain.usecase.fcmnotification.EnqueueUploadFCMTokenWorkerUseCase
import com.trio.stride.domain.usecase.profile.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val getUserUseCase: GetUserUseCase,
    private val enqueueUploadFCMTokenWorkerUseCase: EnqueueUploadFCMTokenWorkerUseCase,
    private val enqueueDeleteFCMTokenWorkerUseCase: EnqueueDeleteFCMTokenWorkerUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState.UNKNOWN)
    val authState: StateFlow<AuthState> = _authState

    private val _navigateTo = MutableStateFlow<String?>(null)
    val navigateTo = _navigateTo.asStateFlow()

    private val _isNavigated = MutableStateFlow(false)
    val isNavigated = _isNavigated.asStateFlow()

    init {
        viewModelScope.launch {
            enqueueDeleteFCMTokenWorkerUseCase.invoke()
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
                AuthState.UNKNOWN
                if (response is Resource.Success) {
                    _authState.value =
                        if (response.data.dob.isBlank()) AuthState.AUTHORIZED_NOT_INITIALIZED else AuthState.AUTHORIZED
                    if (authState.value == AuthState.AUTHORIZED) {
                        enqueueUploadFCMTokenWorkerUseCase.invoke()
                    }
                }
            }
        }
    }

    fun sendNavigate(route: String) {
        _navigateTo.value = route
    }

    fun clearNavigateTo() {
//        _navigateTo.value = null
        _isNavigated.value = true
    }

    fun resetNavigateTo() {
        _navigateTo.value = null
    }

    enum class AuthState { UNKNOWN, AUTHORIZED, AUTHORIZED_NOT_INITIALIZED, UNAUTHORIZED }
}