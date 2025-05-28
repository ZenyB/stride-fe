package com.trio.stride.ui.screens.progress.detail

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.trio.stride.base.BaseViewModel
import com.trio.stride.base.Resource
import com.trio.stride.data.remote.dto.ProgressActivityDto
import com.trio.stride.domain.usecase.progress.GetProgressActivityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgressActivityViewModel @Inject constructor(
    private val getProgressActivityUseCase: GetProgressActivityUseCase,
) : BaseViewModel<LoadingState>() {
    private var _activityData = MutableStateFlow<ProgressActivityDto?>(null)
    val activityData: StateFlow<ProgressActivityDto?> = _activityData

    fun getProgressActivity(sportId: String, fromDate: Long, toDate: Long) {
        viewModelScope.launch {
            Log.d("Progress", "Loading local progress")
            getProgressActivityUseCase(sportId, fromDate, toDate)
                .collectLatest { res ->
                    when (res) {
                        is Resource.Loading -> {
                            setState { LoadingState.Loading }
                        }

                        is Resource.Success -> {
                            _activityData.value = res.data
                            setState { LoadingState.Idle }
                        }

                        is Resource.Error -> {
                            setState {
                                LoadingState.Error(
                                    res.error.message ?: "An error occurred"
                                )

                            }
                        }
                    }
                }
        }
    }

    override fun createInitialState(): LoadingState {
        return LoadingState.Idle
    }
}