package com.trio.stride.domain.usecase.identity

import com.trio.stride.data.dto.SignUpRequest
import com.trio.stride.domain.repository.IdentityRepository
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
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
