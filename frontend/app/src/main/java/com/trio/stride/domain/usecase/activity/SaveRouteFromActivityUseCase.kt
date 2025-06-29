package com.trio.stride.domain.usecase.activity

import com.trio.stride.data.remote.dto.SuccessResponse
import com.trio.stride.domain.repository.RouteRepository
import com.trio.stride.ui.utils.parseErrorResponse
import javax.inject.Inject

class SaveRouteFromActivityUseCase @Inject constructor(
    private val repository: RouteRepository
) {
    suspend operator fun invoke(
        activityId: String,
        routeName: String
    ): Result<SuccessResponse?> {
        return try {
            val response = repository.saveRouteFromActivity(activityId, routeName)
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