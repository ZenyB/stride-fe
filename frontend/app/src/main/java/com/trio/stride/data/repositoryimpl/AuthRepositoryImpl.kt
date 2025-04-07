package com.trio.stride.data.repositoryimpl

import android.os.Build
import androidx.annotation.RequiresApi
import com.trio.stride.data.apiservice.user.UserApi
import com.trio.stride.data.dto.AuthResponseDto
import com.trio.stride.data.dto.LoginRequestDto
import com.trio.stride.data.mapper.toDomain
import com.trio.stride.domain.model.AuthInfo
import com.trio.stride.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: UserApi
) : AuthRepository {
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun login(email: String, password: String): AuthInfo {
        val response = api.login(LoginRequestDto(email, password))

        return when (response) {
            is AuthResponseDto.WithToken -> response.toDomain()
            is AuthResponseDto.WithUserIdentity -> AuthInfo.WithUserIdentity(response.userIdentityId)
        }
    }
}