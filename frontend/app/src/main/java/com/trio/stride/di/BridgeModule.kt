package com.trio.stride.di

import com.trio.stride.data.ApiConstants
import com.trio.stride.data.datastoremanager.FCMTokenManager
import com.trio.stride.data.datastoremanager.TokenManager
import com.trio.stride.data.remote.apiservice.fcmnotification.FCMNotificationApi
import com.trio.stride.data.remote.apiservice.file.FileApi
import com.trio.stride.data.remote.apiservice.notification.NotificationApi
import com.trio.stride.domain.repository.FCMNotificationRepository
import com.trio.stride.domain.repository.FileRepository
import com.trio.stride.domain.repository.NotificationRepository
import com.trio.stride.domain.usecase.fcmnotification.DeleteFCMTokenUseCase
import com.trio.stride.domain.usecase.fcmnotification.RefreshAndSaveFCMTokenUseCase
import com.trio.stride.domain.usecase.fcmnotification.SaveFCMTokenUseCase
import com.trio.stride.domain.usecase.file.UploadFileUseCase
import com.trio.stride.domain.usecase.notification.GetNotificationsUseCase
import com.trio.stride.domain.usecase.notification.MakeSeenAllNotificationsUseCase
import com.trio.stride.domain.usecase.notification.MakeSeenNotificationUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BridgeModule {

    @Provides
    @BridgeBaseUrl
    fun provideRetrofitBridgeUrl(tokenManager: TokenManager): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val authInterceptor = Interceptor { chain ->
            val original = chain.request()
            val token = runBlocking { tokenManager.getAccessToken().firstOrNull() }

            val requestBuilder = original.newBuilder()
            if (!token.isNullOrBlank()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }

            val request = requestBuilder.build()
            chain.proceed(request)
        }
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(ApiConstants.BRIDGE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideFileApi(@BridgeBaseUrl retrofit: Retrofit): FileApi {
        return retrofit.create(FileApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUploadFileUseCase(fileRepository: FileRepository): UploadFileUseCase {
        return UploadFileUseCase(fileRepository)
    }

    @Provides
    @Singleton
    fun provideFCMNotificationApi(@BridgeBaseUrl retrofit: Retrofit): FCMNotificationApi {
        return retrofit.create(FCMNotificationApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSaveFCMUseCase(fcmNotificationRepository: FCMNotificationRepository): SaveFCMTokenUseCase {
        return SaveFCMTokenUseCase(fcmNotificationRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteFCMTokenUseCase(fcmNotificationRepository: FCMNotificationRepository): DeleteFCMTokenUseCase {
        return DeleteFCMTokenUseCase(fcmNotificationRepository)
    }

    @Provides
    @Singleton
    fun provideRefreshAndSaveFCMTokenUseCase(
        fcmTokenManager: FCMTokenManager,
        saveFCMTokenUseCase: SaveFCMTokenUseCase,
        deleteFCMTokenUseCase: DeleteFCMTokenUseCase
    ): RefreshAndSaveFCMTokenUseCase {
        return RefreshAndSaveFCMTokenUseCase(
            fcmTokenManager,
            saveFCMTokenUseCase,
            deleteFCMTokenUseCase
        )
    }

    @Provides
    @Singleton
    fun provideNotificationApi(@BridgeBaseUrl retrofit: Retrofit): NotificationApi {
        return retrofit.create(NotificationApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGetNotificationsUseCase(notificationRepository: NotificationRepository): GetNotificationsUseCase {
        return GetNotificationsUseCase(notificationRepository)
    }

    @Provides
    @Singleton
    fun provideMakeSeenAllNotificationUseCase(notificationRepository: NotificationRepository): MakeSeenAllNotificationsUseCase {
        return MakeSeenAllNotificationsUseCase(notificationRepository)
    }

    @Provides
    @Singleton
    fun provideMakeSeenNotificationUseCase(notificationRepository: NotificationRepository): MakeSeenNotificationUseCase {
        return MakeSeenNotificationUseCase(notificationRepository)
    }
}