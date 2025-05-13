package com.trio.stride.domain.usecase.route

import com.trio.stride.data.remote.dto.RecommendRouteRequest
import com.trio.stride.domain.model.RouteItem
import com.trio.stride.domain.repository.RouteRepository
import com.trio.stride.ui.utils.parseErrorResponse
import javax.inject.Inject

class GetRecommendedRouteUseCase @Inject constructor(
    private val repository: RouteRepository
) {
    suspend operator fun invoke(
        request: RecommendRouteRequest
    ): Result<List<RouteItem>> {
        return try {
            val response = repository.getRecommendRoute(request)
            if (response.isSuccessful) {
                Result.success(response.body()?.data ?: emptyList())
            } else {
                val errorResponse = parseErrorResponse(response.errorBody())
                Result.failure(Exception(errorResponse.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
