package com.trio.stride.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.trio.stride.data.local.entity.ActivityEntity
import com.trio.stride.data.local.entity.ActivityWithSport
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {

    @Transaction
    @Query("SELECT * FROM activities ORDER BY createdAt DESC")
    fun getRecentActivities(): Flow<List<ActivityWithSport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivities(activities: List<ActivityEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityEntity)

    @Query("DELETE FROM activities")
    suspend fun clearAll()
}