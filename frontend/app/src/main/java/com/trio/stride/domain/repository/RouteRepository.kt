package com.trio.stride.domain.repository

import com.trio.stride.data.remote.dto.RecommendRouteRequest
import com.trio.stride.domain.model.RouteItem
import retrofit2.Response

interface RouteRepository {
    suspend fun getRecommendRoute(request: RecommendRouteRequest): Response<List<RouteItem>>
}