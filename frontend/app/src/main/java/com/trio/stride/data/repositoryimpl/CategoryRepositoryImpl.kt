package com.trio.stride.data.repositoryimpl

import com.trio.stride.data.apiservice.category.CategoryApi
import com.trio.stride.domain.model.Category
import com.trio.stride.domain.repository.CategoryRepository
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryApi: CategoryApi
) : CategoryRepository {
    override suspend fun getCategories(
        page: Int?,
        limit: Int?,
        name: String?
    ): List<Category> {
        val response = categoryApi.getCategories(
            page = page,
            limit = limit,
            name = name
        )
        return response.map { categoryDto ->
            Category(
                id = categoryDto.data.id,
                name = categoryDto.data.name
            )
        }
    }
}