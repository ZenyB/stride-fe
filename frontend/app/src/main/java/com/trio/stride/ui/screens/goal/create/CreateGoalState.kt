package com.trio.stride.ui.screens.goal.create

import com.trio.stride.domain.model.GoalTimeFrame
import com.trio.stride.domain.model.GoalType
import com.trio.stride.domain.viewstate.IViewState

data class CreateGoalState(
    val sportId: String? = null,
    val selectedGoalType: GoalType? = null,
    val selectedTimeFrame: GoalTimeFrame? = null,
    val amount: Int? = null,
    val state: SaveGoalState = SaveGoalState.Idle
) : IViewState

sealed class SaveGoalState : IViewState {
    data object Idle : SaveGoalState()
    data object IsSaving : SaveGoalState()
    data class ErrorSaving(val message: String) : SaveGoalState()
    data object Success : SaveGoalState()
}