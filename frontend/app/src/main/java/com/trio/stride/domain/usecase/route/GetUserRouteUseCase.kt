package com.trio.stride.domain.usecase.route

import com.trio.stride.data.remote.dto.UserRouteRequest
import com.trio.stride.data.remote.dto.UserRouteResponse
import com.trio.stride.domain.repository.RouteRepository
import com.trio.stride.ui.utils.parseErrorResponse
import javax.inject.Inject

class GetUserRouteUseCase @Inject constructor(
    private val repository: RouteRepository
) {
    suspend operator fun invoke(
        request: UserRouteRequest
    ): Result<UserRouteResponse?> {
        return try {
            val response = repository.getUserRoute(request)
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