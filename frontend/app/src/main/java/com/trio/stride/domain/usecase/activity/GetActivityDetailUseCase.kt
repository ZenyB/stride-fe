package com.trio.stride.domain.usecase.activity

import com.trio.stride.domain.model.ActivityDetailInfo
import com.trio.stride.domain.repository.ActivityRepository
import com.trio.stride.ui.components.activity.detail.redShades
import com.trio.stride.ui.utils.parseErrorResponse
import javax.inject.Inject

class GetActivityDetailUseCase @Inject constructor(
    private val repository: ActivityRepository
) {
    suspend operator fun invoke(
        id: String,
    ): Result<ActivityDetailInfo?> {
        return try {
            val response = repository.getActivityDetail(id)
            if (response.isSuccessful) {
                val data = response.body()
                Result.success(
                    data?.copy(heartRateZones = data.heartRateZones?.mapIndexed { index, it ->
                        it.copy(
                            color = redShades[index % redShades.size]
                        )
                    })
                )
            } else {
                val errorResponse = parseErrorResponse(response.errorBody())
                Result.failure(Exception(errorResponse.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}