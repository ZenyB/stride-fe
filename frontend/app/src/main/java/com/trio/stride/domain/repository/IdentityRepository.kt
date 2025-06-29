package com.trio.stride.domain.repository

import com.trio.stride.data.remote.dto.ResetPasswordVerifyResponse
import com.trio.stride.data.remote.dto.SignUpRequest
import com.trio.stride.data.remote.dto.SignUpResponse
import com.trio.stride.data.remote.dto.SuccessResponse
import com.trio.stride.data.remote.dto.VerifyOtpResponse
import retrofit2.Response

interface IdentityRepository {
    suspend fun signUp(request: SignUpRequest): Response<SignUpResponse>

    suspend fun verifyOtp(otpCode: String, userIdentity: String): Response<VerifyOtpResponse>

    suspend fun sendOtp(userIdentity: String): Response<VerifyOtpResponse>

    suspend fun sendOtpResetPassword(username: String): Response<SuccessResponse>

    suspend fun resetPasswordVerify(
        username: String,
        token: String
    ): Response<ResetPasswordVerifyResponse>

    suspend fun changePassword(
        resetPasswordTokenId: String,
        token: String,
        password: String
    ): Response<SuccessResponse>
}