package com.trio.stride.di

import com.trio.stride.data.fcm.FirebaseFCMTokenProvider
import com.trio.stride.domain.fcm.FcmTokenProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Provides
    fun provideFcmTokenProvider(): FcmTokenProvider = FirebaseFCMTokenProvider()
}