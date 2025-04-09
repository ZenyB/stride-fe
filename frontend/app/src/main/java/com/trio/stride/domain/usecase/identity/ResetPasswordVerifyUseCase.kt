package com.trio.stride.domain.usecase.identity

import com.trio.stride.domain.repository.IdentityRepository
import com.trio.stride.ui.utils.parseErrorResponse
import javax.inject.Inject

class ResetPasswordVerifyUseCase @Inject constructor(
    private val repository: IdentityRepository
) {
    suspend operator fun invoke(
        username: String,
        otpCode: String
    ): Result<String> {
        return try {
            val response = repository.resetPasswordVerify(username, otpCode)
            if (response.isSuccessful) {
                Result.success(response.body()?.resetPasswordId ?: "empty value")
            } else {
                val errorResponse = parseErrorResponse(response.errorBody())
                Result.failure(Exception(errorResponse.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}