package com.trio.stride.data.remote.dto

import com.trio.stride.domain.model.GoalItem
import com.trio.stride.domain.model.GoalTimeFrame
import com.trio.stride.domain.model.GoalType

data class CreateGoalDTO(
    val sportId: String = "",
    val type: GoalType? = GoalType.ACTIVITY,
    val timeFrame: GoalTimeFrame? = GoalTimeFrame.WEEKLY,
    val amount: Int? = 0
)

data class CreateGoalResponse(
    val id: String?
)

data class GoalListResponse(
    val data: List<GoalItem>
)

data class UpdateGoalRequestDto(
    val rpe: Int = 0,
    val active: Boolean = true,
)