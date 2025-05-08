package com.trio.stride.data.remote.dto

import com.trio.stride.domain.model.SportMapType

data class RecommendRouteRequest(
    val sportId: String,
    val latitude: Double,
    val longitude: Double,
    val sportMapType: SportMapType,
    val limit: Int
)