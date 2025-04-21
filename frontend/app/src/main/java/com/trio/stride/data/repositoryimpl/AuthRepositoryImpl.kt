package com.trio.stride.data.repositoryimpl

import com.trio.stride.data.apiservice.user.UserApi
import com.trio.stride.data.dto.LoginGoogleRequestDto
import com.trio.stride.data.dto.LoginRequestDto
import com.trio.stride.data.dto.LogoutRequestDTO
import com.trio.stride.data.mapper.toDomain
import com.trio.stride.di.Unauthorized
import com.trio.stride.domain.model.AuthInfo
import com.trio.stride.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    @Unauthorized private val api: UserApi
) : AuthRepository {

    override suspend fun login(email: String, password: String): AuthInfo {
        val response = api.login(LoginRequestDto(email, password))
        return response.toDomain()
    }


    override suspend fun loginWithGoogle(idToken: String): AuthInfo {
        val response = api.loginWithGoogle(LoginGoogleRequestDto(idToken))
        return response.toDomain()
    }


    override suspend fun logout(token: String): Boolean {
        val response = api.logout(LogoutRequestDTO(token))
        return response.data
    }
}