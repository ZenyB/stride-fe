package com.trio.stride.domain.usecase.progress

import com.trio.stride.base.Resource
import com.trio.stride.base.UnknownException
import com.trio.stride.data.remote.dto.ProgressListDto
import com.trio.stride.domain.repository.ProgressRepository
import com.trio.stride.ui.utils.parseErrorResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetProgressOverviewUseCase @Inject constructor(
    private val repository: ProgressRepository
) {
    operator fun invoke(): Flow<Resource<ProgressListDto?>> = flow {
        emit(Resource.Loading())

        try {
            val response = repository.getProgressOverview()
            if (response.isSuccessful) {
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