package com.trio.stride.data.dto

data class CreateActivityRequestDTO(
    val sportId: String = "",
    val routeId: String? = null,
    val name: String = "",
    val description: String = "",
    val movingTimeSeconds: Int = 0,
    val elapsedTimeSeconds: Int = 0,
    val coordinates: List<Coordinate> = emptyList(),
    val heartRates: List<Int> = emptyList(),
    val images: List<String> = emptyList(),
    val rpe: Int = 0,
)

data class Coordinate(
    val coordinate: List<Double>,
    val timestamp: Long
)