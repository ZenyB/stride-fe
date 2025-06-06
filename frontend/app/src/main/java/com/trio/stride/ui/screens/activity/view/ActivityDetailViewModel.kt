package com.trio.stride.ui.screens.activity.view

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.trio.stride.base.BaseViewModel
import com.trio.stride.base.Resource
import com.trio.stride.data.remote.dto.UpdateActivityRequestDto
import com.trio.stride.domain.model.ActivityDetailInfo
import com.trio.stride.domain.model.Sport
import com.trio.stride.domain.usecase.activity.DeleteActivityUseCase
import com.trio.stride.domain.usecase.activity.GetActivityDetailUseCase
import com.trio.stride.domain.usecase.activity.UpdateActivityUseCase
import com.trio.stride.domain.viewstate.IViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityDetailViewModel @Inject constructor(
    private val getActivityDetailUseCase: GetActivityDetailUseCase,
    private val updateActivityUseCase: UpdateActivityUseCase,
    private val deleteActivityUseCase: DeleteActivityUseCase,
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
                        Log.i("SPORT_MAP_TYPE_EDIT", data.sport.toString())
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

    fun resetState() {
        setState { ActivityDetailState.Idle }
    }

    fun updateActivity(dto: UpdateActivityRequestDto, sport: Sport, refreshActivity: () -> Unit) {
        if (item.value == null) {
            setState { ActivityDetailState.Error("Activity Not Found") }
            return
        }

        item.value?.let {
            viewModelScope.launch {
                updateActivityUseCase.invoke(
                    it.id,
                    dto
                ).collectLatest { response ->
                    when (response) {
                        is Resource.Loading -> setState { ActivityDetailState.Loading }
                        is Resource.Success -> {
                            getActivityDetail(it.id)
                            refreshActivity()
                        }

                        is Resource.Error -> {
                            setState { ActivityDetailState.Error(response.error.message.toString()) }
                        }
                    }
                }
            }
        }
    }

    fun deleteActivity() {
        if (item.value == null) {
            setState { ActivityDetailState.Error("Activity Not Found") }
            return
        }

        item.value?.let {
            viewModelScope.launch {
                deleteActivityUseCase.invoke(it.id).collectLatest { response ->
                    when (response) {
                        is Resource.Loading -> setState { ActivityDetailState.Loading }
                        is Resource.Success -> {
                            setState { ActivityDetailState.Deleted }
                        }

                        is Resource.Error -> {
                            setState { ActivityDetailState.Error(response.error.message.toString()) }
                        }
                    }
                }
            }
        }
    }

    fun openEditView() {
        setState { ActivityDetailState.Edit }
    }

    fun savingRoute() {
        setState {
            ActivityDetailState.SavingRoute
        }
    }

    fun discardEdit() {
        setState { ActivityDetailState.Idle }
    }
}

sealed class ActivityDetailState : IViewState {
    data object Idle : ActivityDetailState()
    data object Loading : ActivityDetailState()
    data object Edit : ActivityDetailState()
    data object Deleted : ActivityDetailState()
    data object SavingRoute : ActivityDetailState()
    data class Error(val message: String) : ActivityDetailState()
}
