package com.trio.stride.domain.repository

import com.trio.stride.data.remote.dto.RecommendRouteRequest
import com.trio.stride.data.remote.dto.RecommendRouteResponse
import com.trio.stride.data.remote.dto.SuccessResponse
import retrofit2.Response

interface RouteRepository {
    suspend fun getRecommendRoute(request: RecommendRouteRequest): Response<RecommendRouteResponse>
    suspend fun saveRouteFromActivity(activityId: String): Response<SuccessResponse>
}