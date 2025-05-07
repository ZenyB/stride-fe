package com.trio.stride.data.apiservice.activity

import com.trio.stride.base.Resource
import com.trio.stride.data.ApiConstants
import com.trio.stride.data.dto.CreateActivityRequestDTO
import com.trio.stride.data.dto.UpdateActivityRequestDto
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ActivityApi {

    @POST(ApiConstants.ACTIVITY)
    suspend fun createActivity(
        @Body requestDTO: CreateActivityRequestDTO
    ): Resource<Boolean>

    @PUT("${ApiConstants.ACTIVITY}/{id}")
    suspend fun updateActivity(
        @Path("id") id: String,
        @Body requestDTO: UpdateActivityRequestDto
    ): Resource<Boolean>
}