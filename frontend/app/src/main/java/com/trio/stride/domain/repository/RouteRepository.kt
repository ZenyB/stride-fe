package com.trio.stride.domain.repository

import com.trio.stride.data.remote.dto.RecommendRouteRequest
import com.trio.stride.data.remote.dto.RecommendRouteResponse
import com.trio.stride.data.remote.dto.SuccessResponse
import com.trio.stride.data.remote.dto.UserRouteRequest
import com.trio.stride.data.remote.dto.UserRouteResponse
import retrofit2.Response

interface RouteRepository {
    suspend fun getRecommendRoute(request: RecommendRouteRequest): Response<RecommendRouteResponse>
    suspend fun saveRouteFromActivity(activityId: String): Response<SuccessResponse>
    suspend fun getUserRoute(
        request: UserRouteRequest
    ): Response<UserRouteResponse>
}