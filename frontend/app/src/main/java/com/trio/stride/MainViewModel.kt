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

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    init {
        viewModelScope.launch {
            tokenManager.accessToken.collect { token ->
                _isLoggedIn.value = !token.isNullOrEmpty()
            }
        }
    }
}