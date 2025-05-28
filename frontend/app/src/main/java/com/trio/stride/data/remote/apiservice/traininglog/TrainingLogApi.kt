package com.trio.stride.data.remote.apiservice.traininglog

import com.trio.stride.data.ApiConstants
import com.trio.stride.data.remote.dto.TrainingLogsResponseDto
import com.trio.stride.ui.utils.getStartOf12WeeksInMillis
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.LocalDate
import java.time.ZoneId

interface TrainingLogApi {
    @GET(ApiConstants.TRAINING_LOGS)
    suspend fun getTrainingLog(
        @Query("fromDate") fromDate: Long = getStartOf12WeeksInMillis(),
        @Query("toDate") toDate: Long = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli(),
        @Query("sportIds") sportIds: List<String>? = null,
    ): TrainingLogsResponseDto
}