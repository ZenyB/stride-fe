package com.trio.stride.ui.screens.activity.view

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.trio.stride.base.BaseViewModel
import com.trio.stride.domain.model.ActivityDetailInfo
import com.trio.stride.domain.usecase.activity.GetActivityDetailUseCase
import com.trio.stride.domain.viewstate.IViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityDetailViewModel @Inject constructor(
    private val getActivityDetailUseCase: GetActivityDetailUseCase,
) : BaseViewModel<ActivityDetailState>() {
    private val _item = MutableStateFlow<ActivityDetailInfo?>(null)
    val item: StateFlow<ActivityDetailInfo?> = _item

    fun getActivityDetail(id: String) {
        Log.d("okhttp", "Getting activities")
        setState { ActivityDetailState.Loading }

        viewModelScope.launch {
            val result =
                getActivityDetailUseCase(id)
            result
                .onSuccess { data ->
                    setState { ActivityDetailState.Idle }
                    if (data != null) {
                        _item.value = data
                    }
                }
                .onFailure {
                    setState { ActivityDetailState.Error(it.message ?: "An error occurred") }
                }
        }
    }

    override fun createInitialState(): ActivityDetailState {
        return ActivityDetailState.Idle
    }
}

sealed class ActivityDetailState : IViewState {
    data object Idle : ActivityDetailState()
    data object Loading : ActivityDetailState()
    data class Error(val message: String) : ActivityDetailState()
}