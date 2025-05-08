package com.trio.stride.domain.usecase.identity

import com.trio.stride.domain.repository.IdentityRepository
import com.trio.stride.ui.utils.parseErrorResponse
import javax.inject.Inject

class VerifyOtpUseCase @Inject constructor(
    private val repository: IdentityRepository
) {
    suspend operator fun invoke(
        userIdentity: String,
        otpCode: String
    ): Result<String> {
        return try {
            val response = repository.verifyOtp(otpCode, userIdentity)
            if (response.isSuccessful) {
                val message = if (response.body()?.data == true) "OTP Verified" else ""
                Result.success(message)
            } else {
                val errorResponse = parseErrorResponse(response.errorBody())
                Result.failure(Exception(errorResponse.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
