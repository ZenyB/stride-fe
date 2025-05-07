package com.trio.stride.domain.repository

import com.trio.stride.data.dto.CreateActivityRequestDTO
import com.trio.stride.data.dto.UpdateActivityRequestDto

interface ActivityRepository {
    suspend fun createActivity(request: CreateActivityRequestDTO): Boolean
    suspend fun updateActivity(request: UpdateActivityRequestDto, id: String): Boolean
}