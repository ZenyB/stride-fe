package com.trio.stride.data.apiservice.activity

import com.trio.stride.base.Resource
import com.trio.stride.data.ApiConstants
import com.trio.stride.data.dto.ActivityListDto
import com.trio.stride.data.dto.CreateActivityRequestDTO
import com.trio.stride.data.dto.SuccessResponse
import com.trio.stride.domain.model.ActivityDetailInfo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ActivityApi {

    @POST(ApiConstants.ACTIVITY)
    suspend fun createActivity(
        @Body requestDTO: CreateActivityRequestDTO
    ): Resource<Boolean>

    @GET(ApiConstants.ACTIVITY_LIST)
    suspend fun getAllActivity(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
    ): Response<ActivityListDto>

    @GET("${ApiConstants.ACTIVITY}/{id}")
    suspend fun getActivityDetail(
        @Path("id") id: String
    ): Response<ActivityDetailInfo>

    @POST("${ApiConstants.ACTIVITY}/{id}/routes")
    suspend fun saveRouteFromActivity(
        @Path("id") id: String
    ): Response<SuccessResponse>
}