package com.trio.stride.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.trio.stride.data.local.dao.CategoryDao
import com.trio.stride.data.local.dao.CurrentSportDao
import com.trio.stride.data.local.dao.RouteFilterSportDao
import com.trio.stride.data.local.dao.SportDao
import com.trio.stride.data.local.entity.CategoryEntity
import com.trio.stride.data.local.entity.CurrentSportEntity
import com.trio.stride.data.local.entity.RouteFilterSportEntity
import com.trio.stride.data.local.entity.SportEntity

@Database(
    entities = [
        SportEntity::class,
        CategoryEntity::class,
        CurrentSportEntity::class,
        RouteFilterSportEntity::class,
    ], version = 2
)
abstract class StrideDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun sportDao(): SportDao
    abstract fun currentSportDao(): CurrentSportDao
    abstract fun routeFilterSportDao(): RouteFilterSportDao
}