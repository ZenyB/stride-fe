package com.trio.stride.data.remote.apiservice.progress

import com.trio.stride.data.ApiConstants
import com.trio.stride.data.remote.dto.ProgressActivityDto
import com.trio.stride.data.remote.dto.ProgressDetailDto
import com.trio.stride.data.remote.dto.ProgressListDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ProgressApi {
    @GET(ApiConstants.PROGRESS_LIST)
    suspend fun getProgressList(
    ): Response<ProgressListDto>

    @GET(ApiConstants.PROGRESS_DETAIL)
    suspend fun getProgressDetail(
        @Query("sportId") sportId: String? = null,
    ): Response<ProgressDetailDto>

    @GET(ApiConstants.PROGRESS_ACTIVITY)
    suspend fun getProgressActivity(
        @Query("sportId") sportId: String? = null,
        @Query("fromDate") fromDate: Long? = null,
        @Query("toDate") toDate: Long? = null,
    ): Response<ProgressActivityDto>
}