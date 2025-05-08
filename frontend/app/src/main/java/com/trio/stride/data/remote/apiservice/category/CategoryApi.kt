package com.trio.stride.data.remote.apiservice.category

import com.trio.stride.data.ApiConstants
import com.trio.stride.data.remote.dto.GetCategoryResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface CategoryApi {
    @GET(ApiConstants.CATEGORY)
    suspend fun getCategories(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("name") name: String? = null,
    ): GetCategoryResponseDto
}