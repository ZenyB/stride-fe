package com.trio.stride.data.remote.apiservice.activity

import com.trio.stride.base.Resource
import com.trio.stride.data.ApiConstants
import com.trio.stride.data.remote.dto.ActivityListDto
import com.trio.stride.data.remote.dto.CreateActivityRequestDTO
import com.trio.stride.data.remote.dto.UpdateActivityRequestDto
import com.trio.stride.domain.model.ActivityDetailInfo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ActivityApi {

    @POST(ApiConstants.ACTIVITY)
    suspend fun createActivity(
        @Body requestDTO: CreateActivityRequestDTO,
        @Header("X-User-Timezone") timezone: String = "Asia/Ho_Chi_Minh",
    ): Resource<Boolean>

    @PUT("${ApiConstants.ACTIVITY}/{id}")
    suspend fun updateActivity(
        @Path("id") id: String,
        @Body requestDTO: UpdateActivityRequestDto
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
}