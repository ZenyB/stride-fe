package com.trio.stride.domain.model

data class ActivityInfo(
    val sportId: String = ""
)

data class Coordinate(
    val coordinate: List<Double> = listOf<Double>(0.0, 0.0),
    val timeStamp: Long = 0
)

data class ActivityMetric(
    val time: Long = 0,
    val avgSpeed: Float = 0f,
    val distance: Double = 0.0,
)