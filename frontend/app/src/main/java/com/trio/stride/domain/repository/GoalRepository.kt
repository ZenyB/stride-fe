package com.trio.stride.domain.repository

import com.trio.stride.data.remote.dto.CreateGoalDTO
import com.trio.stride.data.remote.dto.CreateGoalResponse
import com.trio.stride.data.remote.dto.GoalListResponse
import com.trio.stride.data.remote.dto.SuccessResponse
import com.trio.stride.data.remote.dto.UpdateGoalRequestDto
import retrofit2.Response

interface GoalRepository {
    suspend fun createGoal(request: CreateGoalDTO): Response<CreateGoalResponse>
    suspend fun getUserGoal(): Response<GoalListResponse>
    suspend fun deleteGoal(id: String): Response<SuccessResponse>
    suspend fun updateGoal(id: String, request: UpdateGoalRequestDto): Response<SuccessResponse>
}