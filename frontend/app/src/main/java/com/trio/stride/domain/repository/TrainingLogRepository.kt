package com.trio.stride.domain.repository

import com.trio.stride.data.remote.dto.TrainingLogFilterDto
import com.trio.stride.domain.model.TrainingLogs

interface TrainingLogRepository {
    suspend fun getTrainingLogs(filter: TrainingLogFilterDto): TrainingLogs
}