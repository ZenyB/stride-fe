package com.trio.stride.domain.usecase.identity

import com.trio.stride.data.remote.dto.SignUpRequest
import com.trio.stride.domain.repository.IdentityRepository
import com.trio.stride.ui.utils.parseErrorResponse
import javax.inject.Inject


class SignUpUseCase @Inject constructor(
    private val repository: IdentityRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<String> {
        return try {
            val response = repository.signUp(SignUpRequest(email, password))
            if (response.isSuccessful) {
                Result.success(response.body()?.userIdentityId ?: "empty value")
            } else {
                val errorResponse = parseErrorResponse(response.errorBody())
                Result.failure(Exception(errorResponse.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

