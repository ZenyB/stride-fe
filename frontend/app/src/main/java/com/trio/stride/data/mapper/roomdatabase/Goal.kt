package com.trio.stride.data.mapper.roomdatabase

import com.trio.stride.data.local.entity.GoalEntity
import com.trio.stride.data.local.entity.GoalHistoryEntity
import com.trio.stride.data.local.entity.GoalWithHistoriesAndSport
import com.trio.stride.domain.model.Category
import com.trio.stride.domain.model.GoalHistoryItem
import com.trio.stride.domain.model.GoalItem
import com.trio.stride.domain.model.Sport

fun GoalItem.toEntity(): GoalEntity = GoalEntity(
    id = id,
    sportId = sport.id,
    type = type,
    timeFrame = timeFrame,
    amountGain = amountGain,
    amountGoal = amountGoal,
    isActive = isActive
)

fun GoalItem.toHistoryEntities(): List<GoalHistoryEntity> =
    histories?.map {
        GoalHistoryEntity(
            goalId = id,
            date = it.date,
            amountGain = it.amountGain,
            amountGoal = it.amountGoal
        )
    } ?: emptyList()


fun GoalWithHistoriesAndSport.toModel(): GoalItem = GoalItem(
    id = goal.id,
    sport = sport?.toModel(Category()) ?: Sport(id = "", name = "", image = ""),
    type = goal.type,
    timeFrame = goal.timeFrame,
    amountGain = goal.amountGain,
    amountGoal = goal.amountGoal,
    isActive = goal.isActive,
    histories = histories.map {
        GoalHistoryItem(
            date = it.date,
            amountGain = it.amountGain,
            amountGoal = it.amountGoal
        )
    }
)
