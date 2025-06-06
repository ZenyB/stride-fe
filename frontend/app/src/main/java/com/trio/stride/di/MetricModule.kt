package com.trio.stride.di

import com.trio.stride.data.ApiConstants
import com.trio.stride.data.datastoremanager.TokenManager
import com.trio.stride.data.remote.apiservice.progress.ProgressApi
import com.trio.stride.data.remote.apiservice.traininglog.TrainingLogApi
import com.trio.stride.domain.repository.ProgressRepository
import com.trio.stride.domain.repository.TrainingLogRepository
import com.trio.stride.domain.usecase.progress.GetProgressActivityUseCase
import com.trio.stride.domain.usecase.traininglog.GetTrainingLogsUseCase
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
object MetricModule {

    @Provides
    @MetricBaseUrl
    fun provideRetrofitMetricUrl(tokenManager: TokenManager): Retrofit {
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

            requestBuilder.addHeader("X-User-Timezone", "Asia/Ho_Chi_Minh")

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
            .baseUrl(ApiConstants.METRIC_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideProgressApi(@MetricBaseUrl retrofit: Retrofit): ProgressApi {
        return retrofit.create(ProgressApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTrainingLogApi(@MetricBaseUrl retrofit: Retrofit): TrainingLogApi {
        return retrofit.create(TrainingLogApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGetProgressActivityUseCase(progressRepository: ProgressRepository): GetProgressActivityUseCase {
        return GetProgressActivityUseCase(progressRepository)
    }

    @Provides
    @Singleton
    fun provideGetTrainingLogsUseCase(trainingLogRepository: TrainingLogRepository): GetTrainingLogsUseCase {
        return GetTrainingLogsUseCase(trainingLogRepository)
    }
}