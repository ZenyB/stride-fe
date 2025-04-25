package com.trio.stride.data.dto

data class RecommendRouteRequest(
    val sportId: String,
    val latitude: Double,
    val longitude: Double,
    val around: Int,
    val limit: Int
)