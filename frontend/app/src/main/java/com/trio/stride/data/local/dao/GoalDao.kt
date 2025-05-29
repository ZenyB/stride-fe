package com.trio.stride.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.trio.stride.data.local.entity.GoalEntity
import com.trio.stride.data.local.entity.GoalHistoryEntity
import com.trio.stride.data.local.entity.GoalWithHistoriesAndSport
import com.trio.stride.data.mapper.roomdatabase.toEntity
import com.trio.stride.data.mapper.roomdatabase.toHistoryEntities
import com.trio.stride.domain.model.GoalItem
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Transaction
    @Query(
        """
    SELECT * FROM goals
    ORDER BY 
        CASE timeFrame
            WHEN 'WEEKLY' THEN 1
            WHEN 'MONTHLY' THEN 2
            WHEN 'ANNUALLY' THEN 3
            ELSE 4
        END
"""
    )
    fun getAllGoals(): Flow<List<GoalWithHistoriesAndSport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistories(histories: List<GoalHistoryEntity>)

    @Transaction
    suspend fun insertGoalWithHistories(goal: GoalEntity, histories: List<GoalHistoryEntity>) {
        insertGoal(goal)
        insertHistories(histories)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoalList(goals: List<GoalEntity>)

    @Transaction
    suspend fun insertGoals(goals: List<GoalItem>) {
        val goalEntities = goals.map { it.toEntity() }
        val historyEntities = goals.flatMap { it.toHistoryEntities() }

        insertGoalList(goalEntities)
        insertHistories(historyEntities)
    }

    @Query("SELECT * FROM goals WHERE id = :goalId LIMIT 1")
    suspend fun getGoalById(goalId: String): GoalEntity?

    @Query("DELETE FROM goals")
    suspend fun clearGoals()

    @Query("DELETE FROM goal_histories WHERE goalId = :goalId")
    suspend fun deleteHistoriesForGoal(goalId: String)

    @Query("DELETE FROM goals WHERE id = :goalId")
    suspend fun deleteGoalById(goalId: String)
}
