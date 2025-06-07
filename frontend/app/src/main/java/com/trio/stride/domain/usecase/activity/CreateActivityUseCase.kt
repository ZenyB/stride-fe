package com.trio.stride.domain.usecase.activity

import com.trio.stride.base.NetworkException
import com.trio.stride.base.Resource
import com.trio.stride.base.UnknownException
import com.trio.stride.data.remote.dto.CreateActivityRequestDTO
import com.trio.stride.domain.model.ActivityItem
import com.trio.stride.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class CreateActivityUseCase(
    private val activityRepository: ActivityRepository
) {
    operator fun invoke(request: CreateActivityRequestDTO): Flow<Resource<ActivityItem>> = flow {
        emit(Resource.Loading())

        try {
            val result = activityRepository.createActivity(request)
            activityRepository.insertActivity(result)
            emit(Resource.Success(result))
        } catch (e: IOException) {
            emit(Resource.Error(NetworkException(e.message.toString())))
        } catch (e: Exception) {
            emit(Resource.Error(UnknownException(e.message.toString())))
        }
    }
}