package com.trio.stride.data.repositoryimpl

import com.trio.stride.data.apiservice.activity.ActivityApi
import com.trio.stride.data.dto.CreateActivityRequestDTO
import com.trio.stride.domain.repository.ActivityRepository
import javax.inject.Inject

class ActivityRepositoryImpl @Inject constructor(
    private val activityApi: ActivityApi
) : ActivityRepository {
    override suspend fun createActivity(request: CreateActivityRequestDTO): Boolean {
        val result = activityApi.createActivity(request)
        return true
    }
}