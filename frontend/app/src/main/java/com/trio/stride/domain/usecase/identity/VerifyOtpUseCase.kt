package com.trio.stride.domain.usecase.identity

import com.trio.stride.data.dto.SignUpRequest
import com.trio.stride.domain.repository.IdentityRepository
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
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
