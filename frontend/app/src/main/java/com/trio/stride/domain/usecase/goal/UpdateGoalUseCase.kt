package com.trio.stride.domain.usecase.goal

import com.trio.stride.data.remote.dto.SuccessResponse
import com.trio.stride.data.remote.dto.UpdateGoalRequestDto
import com.trio.stride.domain.repository.GoalRepository
import com.trio.stride.ui.utils.parseErrorResponse
import javax.inject.Inject

class UpdateGoalUseCase @Inject constructor(
    private val repository: GoalRepository
) {
    suspend operator fun invoke(
        id: String,
        request: UpdateGoalRequestDto
    ): Result<SuccessResponse?> {
        return try {
            val response = repository.updateGoal(id, request)
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