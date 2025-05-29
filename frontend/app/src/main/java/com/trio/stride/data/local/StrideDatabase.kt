package com.trio.stride.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.trio.stride.data.local.dao.AvailableSportDao
import com.trio.stride.data.local.dao.CategoryDao
import com.trio.stride.data.local.dao.CurrentSportDao
import com.trio.stride.data.local.dao.CurrentUserDao
import com.trio.stride.data.local.dao.GoalDao
import com.trio.stride.data.local.dao.ProgressDao
import com.trio.stride.data.local.dao.RouteFilterSportDao
import com.trio.stride.data.local.dao.SportDao
import com.trio.stride.data.local.entity.AvailableSportEntity
import com.trio.stride.data.local.entity.CategoryEntity
import com.trio.stride.data.local.entity.CurrentSportEntity
import com.trio.stride.data.local.entity.CurrentUserEntity
import com.trio.stride.data.local.entity.EquipmentWeightEntity
import com.trio.stride.data.local.entity.GoalEntity
import com.trio.stride.data.local.entity.GoalHistoryEntity
import com.trio.stride.data.local.entity.HeartRateZonesEntity
import com.trio.stride.data.local.entity.ProgressEntity
import com.trio.stride.data.local.entity.RouteFilterSportEntity
import com.trio.stride.data.local.entity.SportEntity

@Database(
    entities = [
        SportEntity::class,
        CategoryEntity::class,
        CurrentSportEntity::class,
        RouteFilterSportEntity::class,
        CurrentUserEntity::class,
        EquipmentWeightEntity::class,
        HeartRateZonesEntity::class,
        AvailableSportEntity::class,
        ProgressEntity::class,
        GoalEntity::class,
        GoalHistoryEntity::class
    ], version = 8
)
abstract class StrideDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun sportDao(): SportDao
    abstract fun currentSportDao(): CurrentSportDao
    abstract fun routeFilterSportDao(): RouteFilterSportDao
    abstract fun currentUserDao(): CurrentUserDao
    abstract fun progressDao(): ProgressDao
    abstract fun availableSportDao(): AvailableSportDao
    abstract fun goalDao(): GoalDao


}