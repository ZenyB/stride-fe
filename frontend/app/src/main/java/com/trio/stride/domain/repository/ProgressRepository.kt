package com.trio.stride.domain.repository

import com.trio.stride.data.local.entity.ProgressEntity
import com.trio.stride.data.remote.dto.ProgressDetailDto
import com.trio.stride.data.remote.dto.ProgressListDto
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface ProgressRepository {
    suspend fun getProgressOverview(): Response<ProgressListDto>
    suspend fun getProgressDetail(sportId: String): Response<ProgressDetailDto>
    fun getProgressOverviewLocal(sportId: String): Flow<List<ProgressEntity>>
    suspend fun upsertProgressOverview(progresses: List<ProgressEntity>)
}