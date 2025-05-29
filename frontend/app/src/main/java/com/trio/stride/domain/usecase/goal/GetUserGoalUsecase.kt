package com.trio.stride.domain.usecase.goal

import com.trio.stride.base.Resource
import com.trio.stride.base.UnknownException
import com.trio.stride.data.remote.dto.GoalListResponse
import com.trio.stride.domain.repository.GoalRepository
import com.trio.stride.ui.utils.parseErrorResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetUserGoalUseCase @Inject constructor(
    private val repository: GoalRepository
) {
    operator fun invoke(
    ): Flow<Resource<GoalListResponse?>> = flow {
        emit(Resource.Loading())
        try {
            val response = repository.getUserGoal()
            if (response.isSuccessful) {
                response.body()?.let { repository.insertGoalList(it.data) }
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