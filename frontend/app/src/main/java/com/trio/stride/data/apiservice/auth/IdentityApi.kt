package com.trio.stride.data.apiservice.auth

import com.trio.stride.data.ApiConstants
import com.trio.stride.data.dto.SignUpRequest
import com.trio.stride.data.dto.SignUpResponse
import com.trio.stride.data.dto.VerifyOtpRequest
import com.trio.stride.data.dto.VerifyOtpResponse
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

}
