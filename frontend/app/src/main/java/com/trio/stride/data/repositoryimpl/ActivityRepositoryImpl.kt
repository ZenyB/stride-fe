package com.trio.stride.data.repositoryimpl

import com.trio.stride.data.remote.apiservice.activity.ActivityApi
import com.trio.stride.data.remote.dto.ActivityListDto
import com.trio.stride.data.remote.dto.CreateActivityRequestDTO
import com.trio.stride.data.remote.dto.UpdateActivityRequestDto
import com.trio.stride.domain.model.ActivityDetailInfo
import com.trio.stride.domain.repository.ActivityRepository
import retrofit2.Response
import javax.inject.Inject

class ActivityRepositoryImpl @Inject constructor(
    private val activityApi: ActivityApi
) : ActivityRepository {
    override suspend fun createActivity(request: CreateActivityRequestDTO): Boolean {
        val result = activityApi.createActivity(request)
        return result.data
    }

    override suspend fun updateActivity(request: UpdateActivityRequestDto, id: String): Boolean {
        val result = activityApi.updateActivity(id, request)
        return true
    }

    override suspend fun getAllActivity(page: Int?, limit: Int?): Response<ActivityListDto> {
        return activityApi.getAllActivity(
            page,
            limit
        )
    }

    override suspend fun getActivityDetail(id: String): Response<ActivityDetailInfo> {
        return activityApi.getActivityDetail(id)
    }

    override suspend fun deleteActivity(id: String): Boolean {
        return activityApi.deleteActivity(id).data
    }
}