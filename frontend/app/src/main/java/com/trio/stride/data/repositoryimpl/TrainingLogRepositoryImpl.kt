package com.trio.stride.data.repositoryimpl

import com.trio.stride.data.mapper.toModel
import com.trio.stride.data.remote.apiservice.traininglog.TrainingLogApi
import com.trio.stride.data.remote.dto.TrainingLogFilterDto
import com.trio.stride.domain.model.TrainingLogItem
import com.trio.stride.domain.repository.TrainingLogRepository
import javax.inject.Inject

class TrainingLogRepositoryImpl @Inject constructor(
    private val trainingLogApi: TrainingLogApi
) : TrainingLogRepository {
    override suspend fun getTrainingLogs(filter: TrainingLogFilterDto): List<TrainingLogItem> {
        return trainingLogApi.getTrainingLog(
            fromDate = filter.fromDate,
            toDate = filter.toDate,
            sportIds = filter.sportIds
        ).data.map { it.toModel() }
    }
}