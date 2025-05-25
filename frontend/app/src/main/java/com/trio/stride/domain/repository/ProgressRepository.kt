package com.trio.stride.domain.repository

import com.trio.stride.data.remote.dto.ProgressDetailDto
import com.trio.stride.data.remote.dto.ProgressDetailRequestDto
import com.trio.stride.data.remote.dto.ProgressListDto
import retrofit2.Response

interface ProgressRepository {
    suspend fun getProgressOverview(): Response<ProgressListDto>
    suspend fun getProgressDetail(request: ProgressDetailRequestDto): Response<ProgressDetailDto>
}