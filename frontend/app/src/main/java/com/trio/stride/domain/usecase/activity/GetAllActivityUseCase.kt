package com.trio.stride.domain.usecase.activity

import com.trio.stride.data.dto.ActivityListDto
import com.trio.stride.domain.repository.ActivityRepository
import com.trio.stride.ui.utils.parseErrorResponse
import javax.inject.Inject

class GetAllActivityUseCase @Inject constructor(
    private val repository: ActivityRepository
) {
    suspend operator fun invoke(
        page: Int? = null,
        limit: Int? = null,
    ): Result<ActivityListDto?> {
        return try {
            val response = repository.getAllActivity(page, limit)
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