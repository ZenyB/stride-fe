package com.trio.stride.data.repositoryimpl

import android.os.Build
import androidx.annotation.RequiresApi
import com.trio.stride.data.apiservice.auth.IdentityApi
import com.trio.stride.data.dto.LoginRequestDto
import com.trio.stride.data.dto.SignUpRequest
import com.trio.stride.data.dto.SignUpResponse
import com.trio.stride.data.dto.VerifyOtpRequest
import com.trio.stride.data.dto.VerifyOtpResponse
import com.trio.stride.data.mapper.toDomain
import com.trio.stride.domain.model.AuthInfo
import com.trio.stride.domain.repository.IdentityRepository
import retrofit2.Response
import javax.inject.Inject

class IdentityRepositoryImpl @Inject constructor(
    private val identityApi: IdentityApi
) : IdentityRepository {
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun signUp(request: SignUpRequest): Response<SignUpResponse> {
        return identityApi.signUp(request)
    }

    override suspend fun verifyOtp(otpCode: String, userIdentity: String): Response<VerifyOtpResponse> {
        return identityApi.verifyOtp(userIdentity, VerifyOtpRequest(otpCode))
    }

    override suspend fun sendOtp(userIdentity: String): Response<VerifyOtpResponse> {
        return identityApi.sendOtp(userIdentity)
    }
}
