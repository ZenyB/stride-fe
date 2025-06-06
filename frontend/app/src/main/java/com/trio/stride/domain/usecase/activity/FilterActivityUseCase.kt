package com.trio.stride.domain.usecase.activity

import com.trio.stride.base.NetworkException
import com.trio.stride.base.Resource
import com.trio.stride.base.UnknownException
import com.trio.stride.domain.model.ActivityFilter
import com.trio.stride.domain.model.ActivityListInfo
import com.trio.stride.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class FilterActivityUseCase @Inject constructor(
    private val activityRepository: ActivityRepository
) {

    operator fun invoke(
        page: Int? = null,
        limit: Int? = null,
        activityFilter: ActivityFilter
    ): Flow<Resource<ActivityListInfo>> = flow {
        emit(Resource.Loading())

        try {
            val result = activityRepository.filterActivity(page, limit, activityFilter)
            emit(Resource.Success(ActivityListInfo(data = result.data, page = result.page)))
        } catch (e: IOException) {
            emit(Resource.Error(NetworkException(e.message.toString())))
        } catch (e: Exception) {
            emit(Resource.Error(UnknownException(e.message.toString())))
        }
    }
}