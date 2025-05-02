package com.trio.stride.ui.screens.activity.detail

import com.trio.stride.base.BaseViewModel
import com.trio.stride.data.datastoremanager.SportManager
import com.trio.stride.data.dto.CreateActivityRequestDTO
import com.trio.stride.data.dto.UpdateActivityRequestDto
import com.trio.stride.domain.model.Activity
import com.trio.stride.domain.model.Sport
import com.trio.stride.domain.viewstate.IViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ActivityDetailViewModel @Inject constructor(
    private val sportManager: SportManager
) : BaseViewModel<ActivityDetailViewModel.ViewState>() {
    override fun createInitialState(): ViewState = ViewState()

    val sports: StateFlow<List<Sport>> = sportManager.sports

    fun updateName(value: String) {
        setState {
            currentState.copy(
                updateActivityDto = updateActivityDto.copy(name = value),
                createActivityDto = createActivityDto.copy(name = value)
            )
        }
    }

    fun updateSport(value: Sport) {
        setState {
            currentState.copy(
                sport = value,
                updateActivityDto = updateActivityDto.copy(sportId = value.id),
                createActivityDto = createActivityDto.copy(sportId = value.id)
            )
        }
    }

    fun updateFeelingRate(value: Int) {
        setState {
            currentState.copy(
                updateActivityDto = updateActivityDto.copy(rpe = value),
                createActivityDto = createActivityDto.copy(rpe = value)
            )
        }
    }

    data class ViewState(
        val isLoading: Boolean = false,
        val activity: Activity = Activity(),
        val sport: Sport = Sport(),
        val updateActivityDto: UpdateActivityRequestDto = UpdateActivityRequestDto(),
        val createActivityDto: CreateActivityRequestDTO = CreateActivityRequestDTO(),
    ) : IViewState

}