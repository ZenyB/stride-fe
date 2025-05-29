package com.trio.stride.domain.repository

import com.trio.stride.data.remote.dto.ActivityListDto
import com.trio.stride.data.remote.dto.CreateActivityRequestDTO
import com.trio.stride.data.remote.dto.UpdateActivityRequestDto
import com.trio.stride.domain.model.ActivityDetailInfo
import com.trio.stride.domain.model.ActivityItem
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface ActivityRepository {
    suspend fun createActivity(request: CreateActivityRequestDTO): Boolean
    suspend fun updateActivity(request: UpdateActivityRequestDto, id: String): Boolean
    suspend fun getAllActivity(
        page: Int? = null,
        limit: Int? = null,
    ): Response<ActivityListDto>

    suspend fun getActivityDetail(
        id: String,
    ): Response<ActivityDetailInfo>

    suspend fun deleteActivity(id: String): Boolean
    suspend fun getRecentLocalActivity(): Flow<List<ActivityItem>>
    suspend fun insertActivityList(items: List<ActivityItem>)
    suspend fun clearAll()
}