package com.trio.stride.ui.screens.activity

import com.trio.stride.domain.viewstate.IViewState

sealed class ActivityListState : IViewState {
    data object Idle : ActivityListState()
    data object Loading : ActivityListState()
    data object Refreshing : ActivityListState()
    data class Error(val message: String) : ActivityListState()
}