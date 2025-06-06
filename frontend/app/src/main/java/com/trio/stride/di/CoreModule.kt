package com.trio.stride.di

import com.trio.stride.data.ApiConstants
import com.trio.stride.data.datastoremanager.MetadataManager
import com.trio.stride.data.datastoremanager.TokenManager
import com.trio.stride.data.remote.apiservice.activity.ActivityApi
import com.trio.stride.data.remote.apiservice.category.CategoryApi
import com.trio.stride.data.remote.apiservice.goal.GoalApi
import com.trio.stride.data.remote.apiservice.sport.SportApi
import com.trio.stride.domain.repository.ActivityRepository
import com.trio.stride.domain.repository.GoalRepository
import com.trio.stride.domain.repository.SportRepository
import com.trio.stride.domain.usecase.activity.CreateActivityUseCase
import com.trio.stride.domain.usecase.activity.DeleteActivityUseCase
import com.trio.stride.domain.usecase.activity.GetAllActivityUseCase
import com.trio.stride.domain.usecase.goal.CreateGoalUseCase
import com.trio.stride.domain.usecase.goal.DeleteUserGoalUseCase
import com.trio.stride.domain.usecase.goal.GetUserGoalUseCase
import com.trio.stride.domain.usecase.goal.UpdateGoalUseCase
import com.trio.stride.domain.usecase.sport.GetSportsUseCase
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
object CoreModule {

    @Provides
    @CoreBaseUrl
    fun provideRetrofitCoreUrl(tokenManager: TokenManager): Retrofit {
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
            .baseUrl(ApiConstants.CORE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @CoreWithTimeBaseUrl
    fun provideRetrofitWithTimeCoreUrl(tokenManager: TokenManager): Retrofit {
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
            .baseUrl(ApiConstants.CORE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideCategoryApi(@CoreBaseUrl retrofit: Retrofit): CategoryApi {
        return retrofit.create(CategoryApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSportApi(@CoreBaseUrl retrofit: Retrofit): SportApi {
        return retrofit.create(SportApi::class.java)
    }

    @Provides
    @Singleton
    fun provideActivityApi(@CoreBaseUrl retrofit: Retrofit): ActivityApi {
        return retrofit.create(ActivityApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGoalApi(@CoreWithTimeBaseUrl retrofit: Retrofit): GoalApi {
        return retrofit.create(GoalApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGetSportsUseCase(
        sportRepository: SportRepository,
        metadataManager: MetadataManager
    ): GetSportsUseCase {
        return GetSportsUseCase(sportRepository, metadataManager)
    }

//    @Provides
//    @Singleton
//    fun provideGetCategoriesUseCase(categoryRepository: CategoryRepository): GetCategoriesUseCase {
//        return GetCategoriesUseCase(categoryRepository)
//    }

    @Provides
    @Singleton
    fun provideCreateActivityUseCase(activityRepository: ActivityRepository): CreateActivityUseCase {
        return CreateActivityUseCase(activityRepository)
    }

    @Provides
    @Singleton
    fun provideGetAllActivity(activityRepository: ActivityRepository): GetAllActivityUseCase {
        return GetAllActivityUseCase(activityRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteActivity(activityRepository: ActivityRepository): DeleteActivityUseCase {
        return DeleteActivityUseCase(activityRepository)
    }

    @Provides
    @Singleton
    fun provideCreateGoalUseCase(goalRepository: GoalRepository): CreateGoalUseCase {
        return CreateGoalUseCase(goalRepository)
    }

    @Provides
    @Singleton
    fun provideGetUserGoalUseCase(goalRepository: GoalRepository): GetUserGoalUseCase {
        return GetUserGoalUseCase(goalRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteUserGoalUseCase(goalRepository: GoalRepository): DeleteUserGoalUseCase {
        return DeleteUserGoalUseCase(goalRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateGoalUseCase(goalRepository: GoalRepository): UpdateGoalUseCase {
        return UpdateGoalUseCase(goalRepository)
    }
}