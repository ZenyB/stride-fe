package com.trio.stride.di

import com.trio.stride.data.ApiConstants
import com.trio.stride.data.datastoremanager.TokenManager
import com.trio.stride.data.remote.apiservice.route.RouteApi
import com.trio.stride.domain.repository.RouteRepository
import com.trio.stride.domain.usecase.activity.SaveRouteFromActivityUseCase
import com.trio.stride.domain.usecase.route.GetRecommendedRouteUseCase
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
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RouteBaseUrl

@Module
@InstallIn(SingletonComponent::class)
object RouteModule {

    @Provides
    @RouteBaseUrl
    fun provideRetrofitRouteUrl(tokenManager: TokenManager): Retrofit {
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
            .baseUrl(ApiConstants.ROUTE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideRouteApi(@RouteBaseUrl retrofit: Retrofit): RouteApi =
        retrofit.create(RouteApi::class.java)


    @Provides
    @Singleton
    fun provideGetRecommendRouteUseCase(repository: RouteRepository) =
        GetRecommendedRouteUseCase(repository)

    @Provides
    @Singleton
    fun provideSaveRouteUseCase(repository: RouteRepository) =
        SaveRouteFromActivityUseCase(repository)
}