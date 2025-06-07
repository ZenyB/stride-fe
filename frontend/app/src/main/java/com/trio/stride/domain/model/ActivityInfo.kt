package com.trio.stride.domain.model

data class ActivityUser(
    val id: String = "",
    val name: String = "",
    val ava: String = ""
)

data class Activity(
    val id: String? = null,
    val name: String = "",
    val description: String = "",
    val sport: Sport = Sport(),
    val user: ActivityUser = ActivityUser(),
    val totalDistance: Double = 0.0,
    val elapsedTimeSeconds: Int = 0,
    val movingTimeSeconds: Int = 0,
    val calories: Int = 0,
    val carbonSaved: Double = 0.0,
    val rpe: Int = 0,
    val coordinates: List<Double> = listOf<Double>(0.0, 0.0),
    val images: List<String> = emptyList(),
    val elevations: List<Int> = emptyList(),
    val elevationGain: Int = 0,
    val maxElevation: Int = 0,
    val speeds: List<Double> = emptyList(),
    val avgSpeed: Double = 0.0,
    val maxSpeed: Double = 0.0,
    val heartRates: List<Int> = emptyList(),
    val heartRateZones: HeartRateZones = HeartRateZones(),
    val avgHearRate: Double = 0.0,
    val maxHeartRate: Double = 0.0,
    val mapImage: String? = "",
    val createdAt: Long = System.currentTimeMillis(),
)

data class ActivityMetric(
    val time: Long = 0,
    val avgSpeed: Float = 0f,
    val distance: Double = 0.0,
)