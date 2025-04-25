package com.trio.stride.domain.model

data class RouteItem(
    val _id: String,
    val sportId: String,
    val name: String,
    val avgTime: Long,
    val avgDistance: Double,
    val totalTime: Long,
    val location: String,
    val images: List<String>,
    val mapImage: String,
    val geometry: String
)
