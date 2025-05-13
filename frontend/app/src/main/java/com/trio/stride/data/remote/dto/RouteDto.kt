package com.trio.stride.data.remote.dto

import com.trio.stride.domain.model.RouteItem
import com.trio.stride.domain.model.SportMapType

data class RecommendRouteRequest(
    val sportId: String,
    val latitude: Double,
    val longitude: Double,
    val sportMapType: SportMapType,
    val limit: Int
)

data class RecommendRouteResponse(
    val data: List<RouteItem>
)

data class UserRouteResponse(
    val data: List<RouteItem>,
    val page: PageDto
)

data class UserRouteRequest(
    val page: Int? = null,
    val limit: Int? = null,
    val sportId: String? = null
)

data class SaveRouteRequest(
    val routeName: String? = null
)