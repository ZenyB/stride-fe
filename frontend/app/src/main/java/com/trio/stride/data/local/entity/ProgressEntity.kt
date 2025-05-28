package com.trio.stride.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "progresses",
    primaryKeys = ["fromDate", "toDate", "sportId"]
)
data class ProgressEntity(
    val sportId: String,
    val fromDate: Long,
    val toDate: Long,
    val distance: Double,
    val elevation: Long,
    val time: Long,
    val numberActivities: Int
)

