package com.trio.stride.di

import android.content.Context
import com.trio.stride.data.local.dao.CurrentUserDao
import com.trio.stride.domain.usecase.fcmnotification.EnqueueDeleteFCMTokenWorkerUseCase
import com.trio.stride.domain.usecase.fcmnotification.EnqueueUploadFCMTokenWorkerUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkManagerModule {

    @Provides
    @Singleton
    fun provideEnqueueUploadFCMTokenWorkerUseCase(
        @ApplicationContext context: Context,
        userDao: CurrentUserDao
    ): EnqueueUploadFCMTokenWorkerUseCase {
        return EnqueueUploadFCMTokenWorkerUseCase(context = context, userDao = userDao)
    }

    @Provides
    @Singleton
    fun provideEnqueueDeleteFCMTokenWorkerUseCase(
        @ApplicationContext context: Context,
    ): EnqueueDeleteFCMTokenWorkerUseCase {
        return EnqueueDeleteFCMTokenWorkerUseCase(context = context)
    }
}
