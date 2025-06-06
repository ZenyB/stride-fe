package com.trio.stride.data.remote.apiservice.route

import com.trio.stride.data.ApiConstants
import com.trio.stride.data.remote.dto.RecommendRouteRequest
import com.trio.stride.data.remote.dto.RecommendRouteResponse
import com.trio.stride.data.remote.dto.SuccessResponse
import com.trio.stride.data.remote.dto.UserRouteResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RouteApi {
    @POST(ApiConstants.RECOMMEND)
    suspend fun getRecommendRoute(@Body request: RecommendRouteRequest):
            Response<RecommendRouteResponse>

    @POST("${ApiConstants.STRIDE_ROUTES}{id}/save")
    suspend fun saveRouteFromActivity(
        @Path("id") id: String,
        @Body routeName: String
    ): Response<SuccessResponse>

    @DELETE("${ApiConstants.STRIDE_ROUTES}{id}")
    suspend fun deleteSavedRoute(
        @Path("id") id: String,
    ): Response<SuccessResponse>

    @GET(ApiConstants.USER_ROUTES)
    suspend fun getUserRoutes(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("sportId") sportId: String? = null,
    ): Response<UserRouteResponse>
}
