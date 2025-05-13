package com.trio.stride.di

import com.trio.stride.data.ApiConstants
import com.trio.stride.data.datastoremanager.TokenManager
import com.trio.stride.data.remote.apiservice.user.UserApi
import com.trio.stride.domain.repository.UserRepository
import com.trio.stride.domain.usecase.profile.GetUserUseCase
import com.trio.stride.domain.usecase.profile.SyncUserUseCase
import com.trio.stride.domain.usecase.profile.UpdateUserUseCase
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
object ProfileModule {


    @Provides
    @ProfileBaseUrl
    fun provideRetrofitProfileUrl(tokenManager: TokenManager): Retrofit {
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
            .baseUrl(ApiConstants.PROFILE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Authorized
    fun provideUserApi(@ProfileBaseUrl retrofit: Retrofit): UserApi =
        retrofit.create(UserApi::class.java)

    @Provides
    @Singleton
    fun provideGetUserCase(repository: UserRepository) =
        GetUserUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdateUserCase(repository: UserRepository, syncUserUseCase: SyncUserUseCase) =
        UpdateUserUseCase(repository, syncUserUseCase)

    @Provides
    @Singleton
    fun provideSyncUserCase(repository: UserRepository) =
        SyncUserUseCase(repository)
}