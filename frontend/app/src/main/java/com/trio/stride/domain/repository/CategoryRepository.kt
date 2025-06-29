package com.trio.stride.domain.repository

import com.trio.stride.domain.model.Category

interface CategoryRepository {
    suspend fun getCategories(
        page: Int? = null,
        limit: Int? = null,
        name: String? = null,
    ): List<Category>

    suspend fun getLocalCategories(): List<Category>
    suspend fun insertCategories(categories: List<Category>)
}
