package com.trio.stride.data.repositoryimpl

import com.trio.stride.data.remote.apiservice.progress.ProgressApi
import com.trio.stride.data.remote.dto.ProgressDetailDto
import com.trio.stride.data.remote.dto.ProgressDetailRequestDto
import com.trio.stride.data.remote.dto.ProgressListDto
import com.trio.stride.domain.repository.ProgressRepository
import retrofit2.Response
import javax.inject.Inject

class ProgressRepositoryImpl @Inject constructor(
    private val progressApi: ProgressApi,
) : ProgressRepository {
    override suspend fun getProgressOverview(): Response<ProgressListDto> {
        return progressApi.getProgressList()
    }

    override suspend fun getProgressDetail(request: ProgressDetailRequestDto): Response<ProgressDetailDto> {
        return progressApi.getProgressDetail(
            sportId = request.sportId,
            type = request.type,
            timeFrame = request.timeFrame
        )
    }
}