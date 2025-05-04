package com.trio.stride.domain.model

import androidx.compose.ui.graphics.Color

data class ActivityItem(
    val id: String,
    val name: String,
    val sport: Sport,
    val totalDistance: Double?,
    val elevationGain: Int?,
    val movingTimeSeconds: Long?,
    val mapImage: String,
    val createdAt: Long,
    val user: ActivityUser,
    val isNeedMap: Boolean
)

data class ActivityUser(
    val id: String,
    val name: String,
    val ava: String,
)

data class ActivityDetailInfo(
    val id: String,
    val name: String,
    val description: String,
    val sport: Sport,
    val user: ActivityUser,
    val totalDistance: Double?,
    val elapsedTimeSeconds: Long,
    val movingTimeSeconds: Long,
    val calories: Long,
    val carbonSaved: Double,
    val rpe: Long,
    val coordinates: List<List<Double>>,
    val images: List<Any?>,
    val mapImage: String,
    val elevations: List<Long>,
    val elevationGain: Long,
    val maxElevation: Long,
    val speeds: List<Double>,
    val avgSpeed: Double,
    val maxSpeed: Double,
    val heartRates: List<Long>,
    val heartRateZones: HeartRateZones?,
    val avgHearRate: Double,
    val maxHearRate: Long,
    val createdAt: Long,
    val isNeedMap: Boolean
)

data class HeartRateInfo(
    val max: Int?,
    val min: Int,
    val duration: Long,
    val title: String,
    val color: Color
)