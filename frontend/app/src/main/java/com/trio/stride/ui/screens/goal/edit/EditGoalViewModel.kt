package com.trio.stride.ui.screens.goal.edit

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.trio.stride.base.BaseViewModel
import com.trio.stride.data.remote.dto.UpdateGoalRequestDto
import com.trio.stride.domain.model.GoalEdit
import com.trio.stride.domain.usecase.goal.UpdateGoalUseCase
import com.trio.stride.domain.viewstate.IViewState
import com.trio.stride.ui.screens.goal.create.SaveGoalState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditGoalViewModel @Inject constructor(
    private val updateGoalUseCase: UpdateGoalUseCase,
) : BaseViewModel<UpdateGoalState>() {
    private val _goalEdit = mutableStateOf<GoalEdit?>(null)

    val goalEdit: State<GoalEdit?> = _goalEdit

    fun setGoalEdit(goal: GoalEdit) {
        _goalEdit.value = goal
        setState {
            currentState.copy(amount = goal.amount)
        }
    }

    fun updateGoal() {
        Log.d("okhttp", "Create goal")
        setState { currentState.copy(state = SaveGoalState.IsSaving) }

        if (goalEdit.value != null) {
            viewModelScope.launch {
                val result =
                    updateGoalUseCase(
                        goalEdit.value!!.id,
                        request =
                        UpdateGoalRequestDto(
                            amount = currentState.amount,
                            active = currentState.active
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
    }

    fun onAmountInputChanged(value: String) {
        val number = value.toIntOrNull()
        setState {
            currentState.copy(
                amount = number ?: 0
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
        val seconds = currentState.amount
        val hours = seconds / 3600
        val remainingMinutes = (seconds % 3600) / 60

        return Pair(hours, remainingMinutes)
    }

    fun resetState() {
        setState {
            currentState.copy(state = SaveGoalState.Idle)
        }
    }

    override fun createInitialState(): UpdateGoalState {
        return UpdateGoalState()
    }
}

data class UpdateGoalState(
    val amount: Int = 0,
    val active: Boolean = true,
    val state: SaveGoalState = SaveGoalState.Idle
) : IViewState
