package com.trio.stride.data.remote.apiservice.route

import com.trio.stride.data.ApiConstants
import com.trio.stride.data.remote.dto.RecommendRouteRequest
import com.trio.stride.data.remote.dto.RecommendRouteResponse
import com.trio.stride.data.remote.dto.SuccessResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface RouteApi {
    @POST(ApiConstants.RECOMMEND)
    suspend fun getRecommendRoute(@Body request: RecommendRouteRequest):
            Response<RecommendRouteResponse>

    @POST("${ApiConstants.STRIDE_ROUTES}{id}/save")
    suspend fun saveRouteFromActivity(
        @Path("id") id: String
    ): Response<SuccessResponse>
}
