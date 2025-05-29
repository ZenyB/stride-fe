package com.trio.stride.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Relation

@Entity(
    tableName = "goal_histories",
    primaryKeys = ["goalId", "date"],
    foreignKeys = [
        ForeignKey(
            entity = GoalEntity::class,
            parentColumns = ["id"],
            childColumns = ["goalId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("goalId")]
)
data class GoalHistoryEntity(
    val goalId: String,
    val date: Long,
    val amountGain: Long,
    val amountGoal: Long
)

data class GoalWithHistoriesAndSport(
    @Embedded val goal: GoalEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "goalId"
    )
    val histories: List<GoalHistoryEntity>,
    @Relation(
        parentColumn = "sportId",
        entityColumn = "id"
    )
    val sport: SportEntity?
)