package com.trio.stride.data.apiservice.activity

import com.trio.stride.base.Resource
import com.trio.stride.data.ApiConstants
import com.trio.stride.data.dto.CreateActivityRequestDTO
import retrofit2.http.Body
import retrofit2.http.POST

interface ActivityApi {

    @POST(ApiConstants.ACTIVITY)
    suspend fun createActivity(
        @Body requestDTO: CreateActivityRequestDTO
    ): Resource<Boolean>
}