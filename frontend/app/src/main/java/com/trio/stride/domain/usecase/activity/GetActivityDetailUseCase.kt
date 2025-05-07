package com.trio.stride.domain.usecase.activity

import androidx.compose.ui.graphics.Color
import com.trio.stride.domain.model.ActivityDetailInfo
import com.trio.stride.domain.repository.ActivityRepository
import com.trio.stride.ui.utils.parseErrorResponse
import javax.inject.Inject

val redShades = listOf(
    Color(0xFFFDB4B5),
    Color(0xFFF6716D),
    Color(0xFFDF2824),
    Color(0xFFB81506),
    Color(0xFF890E11)
)

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