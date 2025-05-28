package com.trio.stride.domain.repository

import com.trio.stride.data.local.entity.ProgressEntity
import com.trio.stride.data.remote.dto.ProgressActivityDto
import com.trio.stride.data.remote.dto.ProgressDetailDto
import com.trio.stride.data.remote.dto.ProgressListDto
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface ProgressRepository {
    suspend fun getProgressOverview(): Response<ProgressListDto>
    suspend fun getProgressDetail(sportId: String): Response<ProgressDetailDto>
    suspend fun getProgressActivity(
        sportId: String,
        fromDate: Long,
        toDate: Long
    ): Response<ProgressActivityDto>

    fun getProgressOverviewLocal(sportId: String): Flow<List<ProgressEntity>>
    suspend fun upsertProgressOverview(progresses: List<ProgressEntity>)
}