package com.trio.stride.domain.repository

import com.trio.stride.domain.model.UserInfo

interface UserRepository {
    suspend fun getUser(): UserInfo
}