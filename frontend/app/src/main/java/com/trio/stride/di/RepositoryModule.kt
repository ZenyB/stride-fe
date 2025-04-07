package com.trio.stride.di

import com.trio.stride.data.repositoryimpl.AuthRepositoryImpl
import com.trio.stride.data.repositoryimpl.IdentityRepositoryImpl
import com.trio.stride.domain.repository.AuthRepository
import com.trio.stride.domain.repository.IdentityRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindIdentityRepository(
        identityRepositoryImpl: IdentityRepositoryImpl
    ): IdentityRepository
}