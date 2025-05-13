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
    val geometry: String,
    val images: List<Any?>,
    val mapImage: String,
    val elevations: List<Long>,
    val elevationGain: Long,
    val maxElevation: Long,
    val speeds: List<Double>,
    val avgSpeed: Double,
    val maxSpeed: Double,
    val heartRates: List<Long>,
    val heartRateZones: List<HeartRateInfo>?,
    val avgHearRate: Double,
    val maxHearRate: Long,
    val distances: List<Double>,
    val createdAt: Long,
    val routeId: String,
)

data class HeartRateInfo(
    val zoneId: String,
    val name: String,
    val min: Long,
    val max: Long?,
    val value: Long,
    val color: Color
)