package com.trio.stride.data.dto

data class CreateActivityRequestDTO(
    val sportId: String,
    val routeId: String?,
    val name: String,
    val description: String,
    val totalDistance: Double,
    val movingTimeSeconds: Int,
    val elapsedTimeSeconds: Int,
    val avgSpeed: Double,
    val coordinates: List<Coordinate>,
    val heartRates: List<Int> = emptyList(),
    val images: List<String> = emptyList(),
    val rpe: Int
)

data class Coordinate(
    val coordinate: List<Double>,
    val timestamp: Long
)