package com.trio.stride.ui.screens.progress

import androidx.lifecycle.viewModelScope
import com.trio.stride.base.BaseViewModel
import com.trio.stride.base.Resource
import com.trio.stride.domain.model.ProgressData
import com.trio.stride.domain.usecase.progress.GetProgressOverviewUseCase
import com.trio.stride.domain.viewstate.IViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProgressOverviewViewModel @Inject constructor(
    private val getProgressOverviewUseCase: GetProgressOverviewUseCase
) : BaseViewModel<OverviewProgressState>() {
    private var _progressData = MutableStateFlow<List<ProgressData>>(emptyList())
    val progressData: StateFlow<List<ProgressData>> = _progressData

    fun getProgress() {
        viewModelScope.launch {
            getProgressOverviewUseCase()
                .collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            setState { OverviewProgressState.Loading }
                        }

                        is Resource.Success -> {
                            val data = resource.data
                            setState { OverviewProgressState.Idle }
                            data?.let {
                                _progressData.value = data.data
                            }
                        }

                        is Resource.Error -> {
                            setState {
                                OverviewProgressState.Error(
                                    resource.error.message ?: "An error occurred"
                                )
                            }
                        }
                    }
                }
        }
    }

    override fun createInitialState(): OverviewProgressState {
        return OverviewProgressState.Idle
    }
}

sealed class OverviewProgressState : IViewState {
    data object Idle : OverviewProgressState()
    data object Loading : OverviewProgressState()
    data class Error(val message: String) : OverviewProgressState()
}