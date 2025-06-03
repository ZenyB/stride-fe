package com.trio.stride.domain.repository

import com.trio.stride.data.local.entity.CurrentUserEntity
import com.trio.stride.data.remote.dto.UpdateUserRequestDto
import com.trio.stride.domain.model.UserInfo

interface UserRepository {
    suspend fun getUser(): UserInfo
    suspend fun updateUser(requestDto: UpdateUserRequestDto): Boolean

    suspend fun getCurrentUser(): UserInfo?
    suspend fun saveCurrentUser(user: CurrentUserEntity)
    suspend fun clearCurrentUser()
}