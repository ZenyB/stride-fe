package com.trio.stride.domain.repository

import com.trio.stride.data.dto.ActivityListDto
import com.trio.stride.data.dto.CreateActivityRequestDTO
import com.trio.stride.data.dto.UpdateActivityRequestDto
import com.trio.stride.data.dto.SuccessResponse
import com.trio.stride.domain.model.ActivityDetailInfo
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

    suspend fun saveRouteFromActivity(activityId: String): Response<SuccessResponse>
}