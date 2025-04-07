package com.trio.stride.domain.repository

import com.trio.stride.domain.model.AuthInfo

interface AuthRepository {
    suspend fun login(email: String, password: String): AuthInfo
}