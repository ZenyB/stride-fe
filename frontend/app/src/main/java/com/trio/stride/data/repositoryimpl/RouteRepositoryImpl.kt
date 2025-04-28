package com.trio.stride.data.repositoryimpl

import com.trio.stride.data.apiservice.route.RouteApi
import com.trio.stride.data.dto.RecommendRouteRequest
import com.trio.stride.domain.model.RouteItem
import com.trio.stride.domain.repository.RouteRepository
import retrofit2.Response
import javax.inject.Inject

class RouteRepositoryImpl @Inject constructor(
    private val routeApi: RouteApi
) : RouteRepository {
    override suspend fun getRecommendRoute(request: RecommendRouteRequest):
            Response<List<RouteItem>> {
        return routeApi.getRecommendRoute(
            request
        )
    }
}