package com.trio.stride.domain.usecase.goal

import com.trio.stride.data.remote.dto.CreateGoalDTO
import com.trio.stride.data.remote.dto.CreateGoalResponse
import com.trio.stride.domain.repository.GoalRepository
import com.trio.stride.ui.utils.parseErrorResponse
import javax.inject.Inject

class CreateGoalUseCase @Inject constructor(
    private val repository: GoalRepository
) {
    suspend operator fun invoke(
        request: CreateGoalDTO
    ): Result<CreateGoalResponse?> {
        return try {
            val response = repository.createGoal(request)
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