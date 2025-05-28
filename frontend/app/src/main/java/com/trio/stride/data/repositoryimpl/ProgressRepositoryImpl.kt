package com.trio.stride.data.repositoryimpl

import com.trio.stride.data.local.dao.ProgressDao
import com.trio.stride.data.local.entity.ProgressEntity
import com.trio.stride.data.remote.apiservice.progress.ProgressApi
import com.trio.stride.data.remote.dto.ProgressDetailDto
import com.trio.stride.data.remote.dto.ProgressListDto
import com.trio.stride.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject

class ProgressRepositoryImpl @Inject constructor(
    private val progressApi: ProgressApi,
    private val progressDao: ProgressDao,
) : ProgressRepository {
    override suspend fun getProgressOverview(): Response<ProgressListDto> {
        return progressApi.getProgressList()
    }

    override suspend fun getProgressDetail(sportId: String): Response<ProgressDetailDto> {
        return progressApi.getProgressDetail(
            sportId = sportId,
        )
    }

    override fun getProgressOverviewLocal(sportId: String): Flow<List<ProgressEntity>> {
        return progressDao.getProgressesBySport(sportId)
    }

    override suspend fun upsertProgressOverview(progresses: List<ProgressEntity>) {
        progressDao.upsertProgresses(progresses)
    }
}