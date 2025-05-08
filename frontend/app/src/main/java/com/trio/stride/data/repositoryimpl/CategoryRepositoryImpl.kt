package com.trio.stride.data.repositoryimpl

import com.trio.stride.data.local.dao.CategoryDao
import com.trio.stride.data.mapper.roomdatabase.toEntity
import com.trio.stride.data.mapper.roomdatabase.toModel
import com.trio.stride.data.remote.apiservice.category.CategoryApi
import com.trio.stride.domain.model.Category
import com.trio.stride.domain.repository.CategoryRepository
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryApi: CategoryApi,
    private val categoryDao: CategoryDao,
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
        return response.data.map { categoryDto ->
            Category(
                id = categoryDto.id,
                name = categoryDto.name
            )
        }
    }

    override suspend fun getLocalCategories(): List<Category> {
        return categoryDao.getAllCategories().map { categoryEntity ->
            categoryEntity.toModel()
        }
    }

    override suspend fun insertCategories(categories: List<Category>) {
        categoryDao.insertCategories(categories.map { category -> category.toEntity() })
    }
}