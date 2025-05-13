package com.trio.stride.data.repositoryimpl

import com.trio.stride.data.remote.apiservice.route.RouteApi
import com.trio.stride.data.remote.dto.RecommendRouteRequest
import com.trio.stride.data.remote.dto.RecommendRouteResponse
import com.trio.stride.data.remote.dto.SuccessResponse
import com.trio.stride.domain.repository.RouteRepository
import retrofit2.Response
import javax.inject.Inject

class RouteRepositoryImpl @Inject constructor(
    private val routeApi: RouteApi
) : RouteRepository {
    override suspend fun getRecommendRoute(request: RecommendRouteRequest):
            Response<RecommendRouteResponse> {
        return routeApi.getRecommendRoute(
            request
        )
    }

    override suspend fun saveRouteFromActivity(activityId: String): Response<SuccessResponse> {
        return routeApi.saveRouteFromActivity(activityId)
    }
}