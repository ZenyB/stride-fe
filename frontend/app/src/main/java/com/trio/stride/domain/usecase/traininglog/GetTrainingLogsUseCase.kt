package com.trio.stride.domain.usecase.traininglog

import com.trio.stride.base.NetworkException
import com.trio.stride.base.Resource
import com.trio.stride.base.UnknownException
import com.trio.stride.data.remote.dto.TrainingLogFilterDto
import com.trio.stride.domain.model.TrainingLogItem
import com.trio.stride.domain.repository.TrainingLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class GetTrainingLogsUseCase @Inject constructor(
    private val trainingLogRepository: TrainingLogRepository
) {
    operator fun invoke(filter: TrainingLogFilterDto): Flow<Resource<List<TrainingLogItem>>> =
        flow {
            emit(Resource.Loading())

            try {
                val result = trainingLogRepository.getTrainingLogs(filter)
                emit(Resource.Success(result))
            } catch (e: IOException) {
                emit(Resource.Error(NetworkException(e.message.toString())))
            } catch (e: Exception) {
                emit(Resource.Error(UnknownException(e.message.toString())))
            }
        }
}