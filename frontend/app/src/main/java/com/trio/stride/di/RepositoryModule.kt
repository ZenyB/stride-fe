package com.trio.stride.di

import com.trio.stride.data.repositoryimpl.ActivityRepositoryImpl
import com.trio.stride.data.repositoryimpl.AuthRepositoryImpl
import com.trio.stride.data.repositoryimpl.FCMNotificationRepositoryImpl
import com.trio.stride.data.repositoryimpl.FileRepositoryImpl
import com.trio.stride.data.repositoryimpl.GoalRepositoryImpl
import com.trio.stride.data.repositoryimpl.IdentityRepositoryImpl
import com.trio.stride.data.repositoryimpl.NotificationRepositoryImpl
import com.trio.stride.data.repositoryimpl.ProgressRepositoryImpl
import com.trio.stride.data.repositoryimpl.RouteRepositoryImpl
import com.trio.stride.data.repositoryimpl.SportRepositoryImpl
import com.trio.stride.data.repositoryimpl.TrainingLogRepositoryImpl
import com.trio.stride.data.repositoryimpl.UserRepositoryImpl
import com.trio.stride.domain.repository.ActivityRepository
import com.trio.stride.domain.repository.AuthRepository
import com.trio.stride.domain.repository.FCMNotificationRepository
import com.trio.stride.domain.repository.FileRepository
import com.trio.stride.domain.repository.GoalRepository
import com.trio.stride.domain.repository.IdentityRepository
import com.trio.stride.domain.repository.NotificationRepository
import com.trio.stride.domain.repository.ProgressRepository
import com.trio.stride.domain.repository.RouteRepository
import com.trio.stride.domain.repository.SportRepository
import com.trio.stride.domain.repository.TrainingLogRepository
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

//    @Binds
//    @Singleton
//    abstract fun bindCategoryRepository(
//        categoryRepositoryImpl: CategoryRepositoryImpl
//    ): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindActivityRepository(
        activityRepositoryImpl: ActivityRepositoryImpl
    ): ActivityRepository

    @Binds
    @Singleton
    abstract fun bindFileRepository(
        fileRepositoryImpl: FileRepositoryImpl
    ): FileRepository

    @Binds
    @Singleton
    abstract fun bindGoalRepository(
        goalRepositoryImpl: GoalRepositoryImpl
    ): GoalRepository

    @Binds
    @Singleton
    abstract fun bindProgressRepository(
        progressRepositoryImpl: ProgressRepositoryImpl
    ): ProgressRepository

    @Binds
    @Singleton
    abstract fun bindTrainingLogRepository(
        trainingLogRepositoryImpl: TrainingLogRepositoryImpl
    ): TrainingLogRepository

    @Binds
    @Singleton
    abstract fun bindFCMNotificationRepository(
        fcmNotificationRepositoryImpl: FCMNotificationRepositoryImpl
    ): FCMNotificationRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        notificationRepositoryImpl: NotificationRepositoryImpl
    ): NotificationRepository
}
