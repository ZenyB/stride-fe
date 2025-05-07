package com.trio.stride.data.repositoryimpl

import com.trio.stride.data.apiservice.activity.ActivityApi
import com.trio.stride.data.dto.ActivityListDto
import com.trio.stride.data.dto.CreateActivityRequestDTO
import com.trio.stride.data.dto.UpdateActivityRequestDto
import com.trio.stride.data.dto.SuccessResponse
import com.trio.stride.domain.model.ActivityDetailInfo
import com.trio.stride.domain.repository.ActivityRepository
import retrofit2.Response
import javax.inject.Inject

class ActivityRepositoryImpl @Inject constructor(
    private val activityApi: ActivityApi
) : ActivityRepository {
    override suspend fun createActivity(request: CreateActivityRequestDTO): Boolean {
        val result = activityApi.createActivity(request)
        return true
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

    override suspend fun saveRouteFromActivity(activityId: String): Response<SuccessResponse> {
        return activityApi.saveRouteFromActivity(activityId)
    }
}