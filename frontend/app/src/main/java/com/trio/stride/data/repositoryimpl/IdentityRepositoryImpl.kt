package com.trio.stride.data.repositoryimpl

import com.trio.stride.data.remote.apiservice.auth.IdentityApi
import com.trio.stride.data.remote.dto.ChangePasswordRequest
import com.trio.stride.data.remote.dto.ResetPasswordVerifyRequest
import com.trio.stride.data.remote.dto.ResetPasswordVerifyResponse
import com.trio.stride.data.remote.dto.SendOTPResetPasswordRequest
import com.trio.stride.data.remote.dto.SignUpRequest
import com.trio.stride.data.remote.dto.SignUpResponse
import com.trio.stride.data.remote.dto.SuccessResponse
import com.trio.stride.data.remote.dto.VerifyOtpRequest
import com.trio.stride.data.remote.dto.VerifyOtpResponse
import com.trio.stride.domain.repository.IdentityRepository
import retrofit2.Response
import javax.inject.Inject

class IdentityRepositoryImpl @Inject constructor(
    private val identityApi: IdentityApi
) : IdentityRepository {

    override suspend fun signUp(request: SignUpRequest): Response<SignUpResponse> {
        return identityApi.signUp(request)
    }

    override suspend fun verifyOtp(
        otpCode: String,
        userIdentity: String
    ): Response<VerifyOtpResponse> {
        return identityApi.verifyOtp(userIdentity, VerifyOtpRequest(otpCode))
    }

    override suspend fun sendOtp(userIdentity: String): Response<VerifyOtpResponse> {
        return identityApi.sendOtp(userIdentity)
    }

    override suspend fun sendOtpResetPassword(username: String): Response<SuccessResponse> {
        return identityApi.sendOtpResetPassword(SendOTPResetPasswordRequest(username))
    }

    override suspend fun resetPasswordVerify(
        username: String,
        token: String
    ): Response<ResetPasswordVerifyResponse> {
        return identityApi.resetPasswordVerify(ResetPasswordVerifyRequest(username, token))
    }

    override suspend fun changePassword(
        resetPasswordTokenId: String,
        token: String,
        password: String
    ): Response<SuccessResponse> {
        return identityApi.changePassword(
            resetPasswordTokenId,
            ChangePasswordRequest(token, password)
        )
    }
}
