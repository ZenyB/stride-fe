package com.trio.stride.data.remote.apiservice.route

import com.trio.stride.data.ApiConstants
import com.trio.stride.data.remote.dto.RecommendRouteRequest
import com.trio.stride.domain.model.RouteItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RouteApi {
    @POST(ApiConstants.RECOMMEND)
    suspend fun getRecommendRoute(@Body request: RecommendRouteRequest):
            Response<List<RouteItem>>

}
