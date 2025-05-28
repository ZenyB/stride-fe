package com.trio.stride.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.trio.stride.data.local.entity.AvailableSportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AvailableSportDao {
    @Upsert()
    suspend fun upsertSports(sports: List<AvailableSportEntity>)

    @Query("SELECT * FROM available_sport")
    fun getAllSports(): Flow<List<AvailableSportEntity>>
}