package com.trio.stride.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.trio.stride.data.local.entity.RouteFilterSportEntity

@Dao
interface RouteFilterSportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSport(sport: RouteFilterSportEntity)

    @Query("SELECT * FROM route_filter_sport LIMIT 1")
    suspend fun getSport(): RouteFilterSportEntity?

    @Query("DELETE FROM route_filter_sport")
    suspend fun deleteRouteFilterSport()
}