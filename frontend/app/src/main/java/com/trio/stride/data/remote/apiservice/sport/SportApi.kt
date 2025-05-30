package com.trio.stride.data.remote.apiservice.sport

import com.trio.stride.data.ApiConstants
import com.trio.stride.data.remote.dto.GetSportResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface SportApi {
    @GET(ApiConstants.SPORT)
    suspend fun getSports(
        @Query("name") name: String? = null,
        @Query("categoryId") categoryId: String? = null
    ): GetSportResponseDto
}