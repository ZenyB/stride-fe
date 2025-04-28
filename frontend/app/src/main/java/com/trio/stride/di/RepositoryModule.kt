package com.trio.stride.di

import com.trio.stride.data.repositoryimpl.AuthRepositoryImpl
import com.trio.stride.data.repositoryimpl.CategoryRepositoryImpl
import com.trio.stride.data.repositoryimpl.IdentityRepositoryImpl
import com.trio.stride.data.repositoryimpl.RouteRepositoryImpl
import com.trio.stride.data.repositoryimpl.SportRepositoryImpl
import com.trio.stride.data.repositoryimpl.UserRepositoryImpl
import com.trio.stride.domain.repository.AuthRepository
import com.trio.stride.domain.repository.CategoryRepository
import com.trio.stride.domain.repository.IdentityRepository
import com.trio.stride.domain.repository.RouteRepository
import com.trio.stride.domain.repository.SportRepository
import com.trio.stride.domain.repository.UserRepository
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

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindRouteRepository(
        routeRepositoryImpl: RouteRepositoryImpl
    ): RouteRepository

    @Binds
    @Singleton
    abstract fun bindSportRepository(
        sportRepositoryImpl: SportRepositoryImpl
    ): SportRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        categoryRepositoryImpl: CategoryRepositoryImpl
    ): CategoryRepository
}
