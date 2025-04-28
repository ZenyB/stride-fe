package com.trio.stride.domain.repository

import com.trio.stride.data.dto.CreateActivityRequestDTO

interface ActivityRepository {
    suspend fun createActivity(request: CreateActivityRequestDTO): Boolean
}