package com.trio.stride.data.repositoryimpl

import com.trio.stride.data.remote.apiservice.goal.GoalApi
import com.trio.stride.data.remote.dto.CreateGoalDTO
import com.trio.stride.data.remote.dto.CreateGoalResponse
import com.trio.stride.data.remote.dto.GoalListResponse
import com.trio.stride.data.remote.dto.SuccessResponse
import com.trio.stride.data.remote.dto.UpdateGoalRequestDto
import com.trio.stride.domain.repository.GoalRepository
import retrofit2.Response
import javax.inject.Inject

class GoalRepositoryImpl @Inject constructor(
    private val goalApi: GoalApi,
) : GoalRepository {
    override suspend fun createGoal(request: CreateGoalDTO): Response<CreateGoalResponse> {
        return goalApi.createGoal(
            request
        )
    }

    override suspend fun getUserGoal(): Response<GoalListResponse> {
        return goalApi.getUserGoals()
    }

    override suspend fun deleteGoal(id: String): Response<SuccessResponse> {
        return goalApi.deleteGoal(id)
    }

    override suspend fun updateGoal(
        id: String,
        request: UpdateGoalRequestDto
    ): Response<SuccessResponse> {
        return goalApi.updateGoal(id, request)
    }
}