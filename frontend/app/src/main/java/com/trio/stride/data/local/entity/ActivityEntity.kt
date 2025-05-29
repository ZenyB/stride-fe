package com.trio.stride.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey val id: String,
    val name: String,
    val sportId: String,
    val totalDistance: Double?,
    val elevationGain: Int?,
    val movingTimeSeconds: Long?,
    val mapImage: String,
    val createdAt: Long,
)

data class ActivityWithSport(
    @Embedded val activity: ActivityEntity,

    @Relation(
        parentColumn = "sportId",
        entityColumn = "id"
    )
    val sport: SportEntity
)