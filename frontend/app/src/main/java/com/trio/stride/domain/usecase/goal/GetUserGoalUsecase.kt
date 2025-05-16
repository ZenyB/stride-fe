package com.trio.stride.domain.usecase.goal

import com.trio.stride.data.remote.dto.GoalListResponse
import com.trio.stride.domain.repository.GoalRepository
import com.trio.stride.ui.utils.parseErrorResponse
import javax.inject.Inject

class GetUserGoalUseCase @Inject constructor(
    private val repository: GoalRepository
) {
    suspend operator fun invoke(
    ): Result<GoalListResponse?> {
        return try {
            val response = repository.getUserGoal()
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