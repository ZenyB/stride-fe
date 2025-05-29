package com.trio.stride.data.repositoryimpl

import com.trio.stride.data.local.dao.GoalDao
import com.trio.stride.data.mapper.roomdatabase.toModel
import com.trio.stride.data.remote.apiservice.goal.GoalApi
import com.trio.stride.data.remote.dto.CreateGoalDTO
import com.trio.stride.data.remote.dto.CreateGoalResponse
import com.trio.stride.data.remote.dto.GoalListResponse
import com.trio.stride.data.remote.dto.SuccessResponse
import com.trio.stride.data.remote.dto.UpdateGoalRequestDto
import com.trio.stride.domain.model.GoalItem
import com.trio.stride.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.Response
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class GoalRepositoryImpl @Inject constructor(
    private val goalApi: GoalApi,
    private val goalDao: GoalDao
) : GoalRepository {
    private var _hasSynced = AtomicBoolean(false)
    private val mutex = Mutex()

    override val hasSynced: AtomicBoolean
        get() = _hasSynced

    override suspend fun createGoal(request: CreateGoalDTO): Response<CreateGoalResponse> {
        mutex.withLock {
            hasSynced.set(false)
        }
        return goalApi.createGoal(
            request
        )
    }

    override suspend fun getUserGoal(): Response<GoalListResponse> {
        mutex.withLock {
            hasSynced.set(true)
        }
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

    override fun getGoalLocal(): Flow<List<GoalItem>> {
        return goalDao.getAllGoals().map { list -> list.map { it.toModel() } }
    }

    override suspend fun insertGoalList(goals: List<GoalItem>) {
        goalDao.clearGoals()
        goalDao.insertGoals(goals)
    }

    override suspend fun updateLocalGoal(goalId: String, update: UpdateGoalRequestDto) {
        val currentGoal = goalDao.getGoalById(goalId) ?: return
        val updatedGoal = currentGoal.copy(
            amountGoal = update.amount.toLong(),
            isActive = update.active
        )
        goalDao.insertGoal(updatedGoal)
    }

    override suspend fun deleteLocalGoal(goalId: String) {
        goalDao.deleteGoalById(goalId)
    }
}