package com.trio.stride.domain.usecase.activity

import com.trio.stride.base.Resource
import com.trio.stride.base.UnknownException
import com.trio.stride.data.remote.dto.ActivityListDto
import com.trio.stride.domain.repository.ActivityRepository
import com.trio.stride.ui.utils.parseErrorResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetAllActivityUseCase @Inject constructor(
    private val repository: ActivityRepository
) {
    operator fun invoke(
        page: Int? = null,
        limit: Int? = null,
    ): Flow<Resource<ActivityListDto?>> = flow {
        emit(Resource.Loading())
        try {
            val response = repository.getAllActivity(page, limit)
            if (response.isSuccessful) {
                val result = response.body()
                if (page == 1) {
                    result?.let {
                        repository.clearAll()
                        repository.insertActivityList(it.data.take(10))
                    }
                }
                emit(Resource.Success(response.body()))
                return@flow
            } else {
                val errorResponse = parseErrorResponse(response.errorBody())
                emit(
                    Resource.Error(
                        error = UnknownException(errorResponse.message),
                    )
                )
            }
        } catch (e: Exception) {
            emit(Resource.Error(UnknownException(e.message.toString())))
        }
    }
}