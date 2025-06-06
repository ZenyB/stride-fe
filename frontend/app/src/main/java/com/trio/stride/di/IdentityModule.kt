package com.trio.stride.di

import com.trio.stride.data.ApiConstants
import com.trio.stride.data.datastoremanager.TokenManager
import com.trio.stride.data.remote.apiservice.auth.IdentityApi
import com.trio.stride.data.remote.apiservice.user.UserApi
import com.trio.stride.domain.repository.AuthRepository
import com.trio.stride.domain.repository.IdentityRepository
import com.trio.stride.domain.usecase.auth.LoginUseCase
import com.trio.stride.domain.usecase.auth.LoginWithGoogleUseCase
import com.trio.stride.domain.usecase.identity.SignUpUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IdentityBaseUrl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ProfileBaseUrl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CoreBaseUrl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CoreWithTimeBaseUrl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BridgeBaseUrl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MetricBaseUrl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Unauthorized

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Authorized

@Module
@InstallIn(SingletonComponent::class)
object IdentityModule {

    @Provides
    @IdentityBaseUrl
    fun provideRetrofitIdentityUrl(): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(ApiConstants.IDENTITY_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Unauthorized
    fun provideUserApi(@IdentityBaseUrl retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideLoginUseCase(
        authRepository: AuthRepository,
        tokenManager: TokenManager
    ): LoginUseCase {
        return LoginUseCase(authRepository, tokenManager)
    }

    @Provides
    @Singleton
    fun provideSignUpApi(@IdentityBaseUrl retrofit: Retrofit): IdentityApi {
        return retrofit.create(IdentityApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSignUpUseCase(identityRepository: IdentityRepository): SignUpUseCase {
        return SignUpUseCase(identityRepository)
    }

    @Provides
    @Singleton
    fun provideLoginWithGoogleUseCase(
        authRepository: AuthRepository,
        tokenManager: TokenManager
    ): LoginWithGoogleUseCase {
        return LoginWithGoogleUseCase(authRepository, tokenManager)
    }
}