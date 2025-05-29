package com.trio.stride.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "goals",
    foreignKeys = [ForeignKey(
        entity = SportEntity::class,
        parentColumns = ["id"],
        childColumns = ["sportId"],
        onDelete = ForeignKey.CASCADE
    )],
)
data class GoalEntity(
    @PrimaryKey val id: String,
    val sportId: String?,
    val type: String,
    val timeFrame: String,
    val amountGain: Long,
    val amountGoal: Long,
    val isActive: Boolean
)