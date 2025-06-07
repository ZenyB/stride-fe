package com.trio.stride.data.repositoryimpl

import com.trio.stride.data.local.dao.ActivityDao
import com.trio.stride.data.local.dao.CurrentUserDao
import com.trio.stride.data.mapper.roomdatabase.toActivityItem
import com.trio.stride.data.mapper.roomdatabase.toEntity
import com.trio.stride.data.remote.apiservice.activity.ActivityApi
import com.trio.stride.data.remote.dto.ActivityListDto
import com.trio.stride.data.remote.dto.CreateActivityRequestDTO
import com.trio.stride.data.remote.dto.UpdateActivityRequestDto
import com.trio.stride.domain.model.ActivityDetailInfo
import com.trio.stride.domain.model.ActivityItem
import com.trio.stride.domain.model.ActivityUser
import com.trio.stride.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.Response
import javax.inject.Inject

class ActivityRepositoryImpl @Inject constructor(
    private val activityApi: ActivityApi,
    private val activityDao: ActivityDao,
    private val currentUserDao: CurrentUserDao
) : ActivityRepository {
    override suspend fun createActivity(request: CreateActivityRequestDTO): ActivityItem {
        val result = activityApi.createActivity(request)
        return result
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

    override suspend fun getRecentLocalActivity(): Flow<List<ActivityItem>> {
        val currentUser = currentUserDao.getCurrentUser()
        val user = if (currentUser != null) ActivityUser(
            id = currentUser.id,
            name = currentUser.name,
            ava = currentUser.ava
        ) else ActivityUser(id = "", name = "", ava = "")
        return activityDao.getRecentActivities().map { list ->
            list.map {
                it.toActivityItem(
                    user
                )
            }
        }
    }

    override suspend fun insertActivityList(items: List<ActivityItem>) {
        activityDao.insertActivities(items.map { it.toEntity() })
    }

    override suspend fun insertActivity(item: ActivityItem) {
        activityDao.insertActivity(item.toEntity())
    }

    override suspend fun clearAll() {
        activityDao.clearAll()
    }
}