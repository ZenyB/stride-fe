package com.trio.stride.ui.screens.signup

import com.trio.stride.domain.viewstate.IViewState

sealed class SignUpViewState : IViewState {
    object Idle : SignUpViewState()
    object Loading : SignUpViewState()
    object WithGoogleSuccess : SignUpViewState()
    data class Success(val userIdentity: String) : SignUpViewState()
    data class Error(val message: String) : SignUpViewState()
}
