package com.trio.stride.data.remote.apiservice.progress

import com.trio.stride.data.ApiConstants
import com.trio.stride.data.remote.dto.ProgressDetailDto
import com.trio.stride.data.remote.dto.ProgressListDto
import com.trio.stride.domain.model.ProgressTimeRange
import com.trio.stride.domain.model.ProgressType
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
        @Query("type") type: String? = ProgressTimeRange.LAST_3_MONTHS.label,
        @Query("timeFrame") timeFrame: String? = ProgressType.DISTANCE.name,
    ): Response<ProgressDetailDto>
}