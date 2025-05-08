package com.trio.stride.data.remote.apiservice.auth

import com.trio.stride.data.ApiConstants
import com.trio.stride.data.remote.dto.ChangePasswordRequest
import com.trio.stride.data.remote.dto.ResetPasswordVerifyRequest
import com.trio.stride.data.remote.dto.ResetPasswordVerifyResponse
import com.trio.stride.data.remote.dto.SendOTPResetPasswordRequest
import com.trio.stride.data.remote.dto.SignUpRequest
import com.trio.stride.data.remote.dto.SignUpResponse
import com.trio.stride.data.remote.dto.SuccessResponse
import com.trio.stride.data.remote.dto.VerifyOtpRequest
import com.trio.stride.data.remote.dto.VerifyOtpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface IdentityApi {
    @POST(ApiConstants.SIGNUP)
    suspend fun signUp(@Body request: SignUpRequest): Response<SignUpResponse>

    @POST("${ApiConstants.USERS}/{userIdentity}/verify")
    suspend fun verifyOtp(
        @Path("userIdentity") userIdentity: String, // Path parameter
        @Body request: VerifyOtpRequest // Body with the OTP code
    ): Response<VerifyOtpResponse>

    @POST("${ApiConstants.USERS}/{userIdentity}/verify/send-otp")
    suspend fun sendOtp(
        @Path("userIdentity") userIdentity: String,
    ): Response<VerifyOtpResponse>

    @POST("${ApiConstants.RESET_PASSWORD}/send-otp")
    suspend fun sendOtpResetPassword(@Body request: SendOTPResetPasswordRequest): Response<SuccessResponse>

    @POST("${ApiConstants.RESET_PASSWORD}/verify")
    suspend fun resetPasswordVerify(@Body request: ResetPasswordVerifyRequest): Response<ResetPasswordVerifyResponse>

    @POST("${ApiConstants.RESET_PASSWORD}/{resetPasswordTokenId}/change-password")
    suspend fun changePassword(
        @Path("resetPasswordTokenId") resetPasswordTokenId: String,
        @Body request: ChangePasswordRequest
    ): Response<SuccessResponse>
}
