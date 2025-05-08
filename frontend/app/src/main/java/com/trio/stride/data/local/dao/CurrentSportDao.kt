package com.trio.stride.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.trio.stride.data.local.entity.CurrentSportEntity

@Dao
interface CurrentSportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCurrentSport(currentSport: CurrentSportEntity)

    @Query("SELECT * FROM current_sport LIMIT 1")
    suspend fun getCurrentSport(): CurrentSportEntity?
}