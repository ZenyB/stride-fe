package com.trio.stride.di

import com.trio.stride.data.ApiConstants
import com.trio.stride.data.apiservice.category.CategoryApi
import com.trio.stride.data.apiservice.sport.SportApi
import com.trio.stride.domain.repository.CategoryRepository
import com.trio.stride.domain.repository.SportRepository
import com.trio.stride.domain.usecase.category.GetCategoriesUseCase
import com.trio.stride.domain.usecase.sport.GetSportsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    @Provides
    @CoreBaseUrl
    fun provideRetrofitCoreUrl(): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
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
    fun provideGetSportsUseCase(sportRepository: SportRepository): GetSportsUseCase {
        return GetSportsUseCase(sportRepository)
    }

    @Provides
    @Singleton
    fun provideGetCategoriesUseCase(categoryRepository: CategoryRepository): GetCategoriesUseCase {
        return GetCategoriesUseCase(categoryRepository)
    }
}