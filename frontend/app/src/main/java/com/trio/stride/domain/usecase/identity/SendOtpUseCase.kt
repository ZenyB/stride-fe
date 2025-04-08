package com.trio.stride.domain.usecase.identity

import com.trio.stride.data.dto.SignUpRequest
import com.trio.stride.domain.repository.IdentityRepository
import com.trio.stride.ui.utils.parseErrorResponse
import javax.inject.Inject

class SendOtpUseCase @Inject constructor(
    private val repository: IdentityRepository
) {
    suspend operator fun invoke(
        userIdentity: String,
    ): Result<String> {
        return try {
            val response = repository.sendOtp(userIdentity)
            if (response.isSuccessful) {
                val message = if (response.body()?.data == true) "OTP sent successfully" else ""
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
