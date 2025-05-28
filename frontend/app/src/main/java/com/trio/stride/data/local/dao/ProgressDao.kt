package com.trio.stride.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.trio.stride.data.local.entity.ProgressEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface ProgressDao {
    @Upsert()
    suspend fun upsertProgresses(progresses: List<ProgressEntity>)

    @Query("SELECT * FROM progresses WHERE sportId = :sportId")
    fun getProgressesBySport(sportId: String): Flow<List<ProgressEntity>>
}