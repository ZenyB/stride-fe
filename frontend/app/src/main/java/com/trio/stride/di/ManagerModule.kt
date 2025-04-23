package com.trio.stride.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.mapbox.maps.Map
import com.trio.stride.data.datastoremanager.MapStyleManager
import com.trio.stride.data.datastoremanager.TokenManager
import com.trio.stride.data.datastoremanager.UserManager
import com.trio.stride.domain.usecase.profile.GetUserUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ManagerModule {

    @Provides
    @Singleton
    fun provideTokenManager(
        dataStore: DataStore<Preferences>
    ): TokenManager {
        return TokenManager(dataStore)
    }

    @Provides
    @Singleton
    fun provideUserManager(
        dataStore: DataStore<Preferences>,
        getUserUseCase: GetUserUseCase
    ): UserManager {
        return UserManager(dataStore, getUserUseCase)
    }

    @Provides
    @Singleton
    fun provideMapStyleManager(
        dataStore: DataStore<Preferences>,
    ): MapStyleManager {
        return MapStyleManager(dataStore)
    }
}