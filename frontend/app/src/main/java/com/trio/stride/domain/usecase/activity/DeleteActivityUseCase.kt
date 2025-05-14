package com.trio.stride.domain.usecase.activity

import com.trio.stride.base.NetworkException
import com.trio.stride.base.Resource
import com.trio.stride.base.UnknownException
import com.trio.stride.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class DeleteActivityUseCase @Inject constructor(
    private val activityRepository: ActivityRepository
) {
    operator fun invoke(id: String): Flow<Resource<Boolean>> =
        flow {
            emit(Resource.Loading())

            try {
                val result = activityRepository.deleteActivity(id)
                emit(Resource.Success(result))
            } catch (e: IOException) {
                emit(Resource.Error(NetworkException(e.message.toString())))
            } catch (e: Exception) {
                if (e.message == "Failed to invoke private com.trio.stride.base.Resource() with no args")
                    emit(Resource.Success(true))
                else emit(Resource.Error(UnknownException(e.message.toString())))
            }

        }
}