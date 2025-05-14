package com.trio.stride.domain.usecase.goal

import com.trio.stride.data.remote.dto.SuccessResponse
import com.trio.stride.domain.repository.GoalRepository
import com.trio.stride.ui.utils.parseErrorResponse
import javax.inject.Inject

class DeleteUserGoalUseCase @Inject constructor(
    private val repository: GoalRepository
) {
    suspend operator fun invoke(
        id: String
    ): Result<SuccessResponse?> {
        return try {
            val response = repository.deleteGoal(id)
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