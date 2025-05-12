package com.trio.stride.di

import android.content.Context
import androidx.room.Room
import com.trio.stride.data.local.StrideDatabase
import com.trio.stride.data.local.dao.CategoryDao
import com.trio.stride.data.local.dao.CurrentSportDao
import com.trio.stride.data.local.dao.CurrentUserDao
import com.trio.stride.data.local.dao.RouteFilterSportDao
import com.trio.stride.data.local.dao.SportDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomDatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): StrideDatabase {
        return Room.databaseBuilder(
            context,
            StrideDatabase::class.java,
            "stride_db"
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    fun provideSportDao(db: StrideDatabase): SportDao = db.sportDao()

    @Provides
    fun provideCategoryDao(db: StrideDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideCurrentSportDao(db: StrideDatabase): CurrentSportDao = db.currentSportDao()

    @Provides
    fun provideRouteFilterSportDao(db: StrideDatabase): RouteFilterSportDao =
        db.routeFilterSportDao()

    @Provides
    fun provideCurrentUserDao(db: StrideDatabase): CurrentUserDao =
        db.currentUserDao()
}