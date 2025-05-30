package com.trio.stride.data.remote.apiservice.traininglog

import com.trio.stride.data.ApiConstants
import com.trio.stride.data.remote.dto.TrainingLogsResponseDto
import com.trio.stride.ui.utils.getEndOfWeekInMillis
import com.trio.stride.ui.utils.getStartOf12WeeksInMillis
import retrofit2.http.GET
import retrofit2.http.Query

interface TrainingLogApi {
    @GET(ApiConstants.TRAINING_LOGS)
    suspend fun getTrainingLog(
        @Query("fromDate") fromDate: Long = getStartOf12WeeksInMillis(),
        @Query("toDate") toDate: Long = getEndOfWeekInMillis(),
        @Query("sportIds") sportIds: List<String>? = null,
    ): TrainingLogsResponseDto
}