package com.trio.stride.domain.usecase.activity

import com.trio.stride.base.NetworkException
import com.trio.stride.base.Resource
import com.trio.stride.base.UnknownException
import com.trio.stride.data.dto.UpdateActivityRequestDto
import com.trio.stride.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class UpdateActivityUseCase @Inject constructor(
    private val activityRepository: ActivityRepository
) {
    operator fun invoke(id: String, requestDto: UpdateActivityRequestDto): Flow<Resource<Boolean>> =
        flow {
            emit(Resource.Loading())
            
            try {
                val result = activityRepository.updateActivity(requestDto, id)
                emit(Resource.Success(result))
            } catch (e: IOException) {
                emit(Resource.Error(NetworkException(e.message.toString())))
            } catch (e: Exception) {
                emit(Resource.Error(UnknownException(e.message.toString())))
            }

        }
}