package com.trio.stride.ui.screens.goal.create

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.trio.stride.base.BaseViewModel
import com.trio.stride.data.datastoremanager.SportManager
import com.trio.stride.data.remote.dto.CreateGoalDTO
import com.trio.stride.domain.model.GoalTimeFrame
import com.trio.stride.domain.model.GoalType
import com.trio.stride.domain.model.Sport
import com.trio.stride.domain.model.SportMapType
import com.trio.stride.domain.usecase.goal.CreateGoalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateGoalViewModel @Inject constructor(
    private val sportManager: SportManager,
    private val createGoalUseCase: CreateGoalUseCase,
) : BaseViewModel<CreateGoalState>() {

    private val _sportsByCategory = sportManager.sportsByCategory
    val sportsByCategory: StateFlow<Map<String, List<Sport>>> = _sportsByCategory

    val defaultSport = _sportsByCategory.value.entries.first { item ->
        item.value.isNotEmpty()
    }.value.getOrNull(0)

    fun createGoal() {
        Log.d("okhttp", "Create goal")
        setState { currentState.copy(state = SaveGoalState.IsSaving) }

        viewModelScope.launch {
            val result =
                createGoalUseCase(
                    request =
                        CreateGoalDTO(
                            sportId = "d81c0cf1-7b18-44d4-8e4f-bbd8408aa45d",
                            type = currentState.selectedGoalType,
                            timeFrame = currentState.selectedTimeFrame,
                            amount = currentState.amount
                        )
                )
            result
                .onSuccess { data ->
                    Log.d("Create goal", "Create success")
                    setState { currentState.copy(state = SaveGoalState.Success) }
                }
                .onFailure {
                    Log.d("Create goal", "Create failed ${it.message}")
                    setState {
                        currentState.copy(
                            state = SaveGoalState.ErrorSaving(
                                it.message ?: "An error occurred"
                            )
                        )
                    }
                }
        }
    }


    fun onGoalTypeSelected(type: GoalType) {
        if (type != uiState.value.selectedGoalType) {
            setState {
                currentState.copy(
                    selectedGoalType = type,
                    amount = 0
                )
            }
        }
    }

    fun onTimeFrameSelected(frame: GoalTimeFrame) {
        if (frame != uiState.value.selectedTimeFrame) {
            setState {
                currentState.copy(
                    selectedTimeFrame = frame,
                    selectedGoalType = null,
                    amount = 0
                )
            }
        }
    }

    fun onSportSelected(sport: Sport) {
        if (sport.id != uiState.value.sportId) {
            if (sport.sportMapType == SportMapType.NO_MAP) {
                if (uiState.value.selectedGoalType == GoalType.DISTANCE || uiState.value.selectedGoalType == GoalType.ELEVATION) {
                    setState {
                        currentState.copy(
                            sportId = sport.id,
                            selectedGoalType = null,
                            amount = 0
                        )
                    }
                    return
                }
            }
            setState {
                currentState.copy(
                    sportId = sport.id
                )
            }
        }
    }

    fun onAmountInputChanged(value: String) {
        val number = value.toIntOrNull()
        setState {
            currentState.copy(
                amount = number
            )
        }
    }

    fun onHourChanged(value: String) {
        val hour = value.toIntOrNull() ?: 0
        val currentTime = getHourAndMinute()
        val newTime = (hour * 3600) + (currentTime.second * 60)
        setState {
            currentState.copy(amount = newTime)
        }
    }

    fun onMinuteChange(value: String) {
        val minute = value.toIntOrNull() ?: 0
        val currentTime = getHourAndMinute()
        val newTime = (currentTime.first * 3600) + (minute * 60)
        setState {
            currentState.copy(amount = newTime)
        }
    }


    fun getHourAndMinute(): Pair<Int, Int> {
        val seconds = currentState.amount ?: 0
        val hours = seconds / 3600
        val remainingMinutes = (seconds % 3600) / 60

        return Pair(hours, remainingMinutes)
    }

    fun resetState() {
        setState {
            currentState.copy(state = SaveGoalState.Idle)
        }
    }

    override fun createInitialState(): CreateGoalState {
        return CreateGoalState(
            selectedTimeFrame = GoalTimeFrame.WEEKLY,
            sportId = defaultSport?.id
        )
    }

}