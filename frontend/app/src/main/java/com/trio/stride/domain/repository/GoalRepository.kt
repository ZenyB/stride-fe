package com.trio.stride.domain.repository

import com.trio.stride.data.remote.dto.CreateGoalDTO
import com.trio.stride.data.remote.dto.CreateGoalResponse
import com.trio.stride.data.remote.dto.GoalListResponse
import com.trio.stride.data.remote.dto.SuccessResponse
import com.trio.stride.data.remote.dto.UpdateGoalRequestDto
import com.trio.stride.domain.model.GoalItem
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import java.util.concurrent.atomic.AtomicBoolean

interface GoalRepository {
    val hasSynced: AtomicBoolean
    suspend fun createGoal(request: CreateGoalDTO): Response<CreateGoalResponse>
    suspend fun getUserGoal(): Response<GoalListResponse>
    suspend fun deleteGoal(id: String): Response<SuccessResponse>
    suspend fun updateGoal(id: String, request: UpdateGoalRequestDto): Response<SuccessResponse>
    fun getGoalLocal(): Flow<List<GoalItem>>
    suspend fun insertGoalList(goals: List<GoalItem>)
    suspend fun updateLocalGoal(goalId: String, update: UpdateGoalRequestDto)
    suspend fun deleteLocalGoal(goalId: String)
}