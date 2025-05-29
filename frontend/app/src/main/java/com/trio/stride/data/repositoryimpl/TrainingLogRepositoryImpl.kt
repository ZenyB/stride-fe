package com.trio.stride.data.repositoryimpl

import com.trio.stride.data.mapper.toModel
import com.trio.stride.data.remote.apiservice.traininglog.TrainingLogApi
import com.trio.stride.data.remote.dto.TrainingLogFilterDto
import com.trio.stride.domain.model.TrainingLogs
import com.trio.stride.domain.repository.TrainingLogRepository
import javax.inject.Inject

class TrainingLogRepositoryImpl @Inject constructor(
    private val trainingLogApi: TrainingLogApi
) : TrainingLogRepository {
    override suspend fun getTrainingLogs(filter: TrainingLogFilterDto): TrainingLogs {
        val result = trainingLogApi.getTrainingLog(
            fromDate = filter.fromDate,
            toDate = filter.toDate,
            sportIds = filter.sportIds
        )
        val trainingLogItems = result.data.map { it.toModel() }
        return TrainingLogs(
            trainingLogs = trainingLogItems,
            metaData = result.metadata.toModel()
        )
    }
}