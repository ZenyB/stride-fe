package com.trio.stride.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "heart_rate_zone")
data class HeartRateZonesEntity(
    @PrimaryKey val userId: String,
    val zone1: Int,
    val zone2: Int,
    val zone3: Int = 0,
    val zone4: Int = 0,
    val zone5: Int = 0,
    val zone6: Int = 0,
)