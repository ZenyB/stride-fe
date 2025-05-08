package com.trio.stride.domain.usecase.activity

import com.trio.stride.data.remote.dto.SuccessResponse
import com.trio.stride.domain.repository.ActivityRepository
import com.trio.stride.ui.utils.parseErrorResponse
import javax.inject.Inject

class SaveRouteFromActivityUseCase @Inject constructor(
    private val repository: ActivityRepository
) {
    suspend operator fun invoke(
        activityId: String,
    ): Result<SuccessResponse?> {
        return try {
            val response = repository.saveRouteFromActivity(activityId)
            if (response.isSuccessful) {
                Result.success(response.body())
            } else {
                val errorResponse = parseErrorResponse(response.errorBody())
                Result.failure(Exception(errorResponse.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}