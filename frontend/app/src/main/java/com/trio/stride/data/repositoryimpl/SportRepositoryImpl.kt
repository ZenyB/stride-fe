package com.trio.stride.data.repositoryimpl

import com.trio.stride.data.local.dao.CategoryDao
import com.trio.stride.data.local.dao.SportDao
import com.trio.stride.data.mapper.roomdatabase.toEntity
import com.trio.stride.data.mapper.roomdatabase.toModel
import com.trio.stride.data.remote.apiservice.sport.SportApi
import com.trio.stride.domain.model.Category
import com.trio.stride.domain.model.Sport
import com.trio.stride.domain.repository.SportRepository
import javax.inject.Inject

class SportRepositoryImpl @Inject constructor(
    private val sportApi: SportApi,
    private val sportDao: SportDao,
    private val categoryDao: CategoryDao,
) : SportRepository {
    override suspend fun getSports(
        page: Int?,
        limit: Int?,
        name: String?,
        categoryId: String?,
    ): List<Sport> {
        val result = sportApi.getSports(page, limit, name, categoryId)
        return result.data.map { response ->
            Sport(
                id = response.id,
                name = response.name,
                category = Category(response.category.id, response.category.name),
                image = response.image,
                sportMapType = response.sportMapType
            )
        }
    }

    override suspend fun getLocalSports(categoryId: String?): List<Sport> {
        return sportDao.getAllSports().mapNotNull { sportEntity ->
            val categoryEntity = categoryDao.getCategoryById(sportEntity.categoryId)
            categoryEntity?.let {
                val category = it.toModel()
                sportEntity.toModel(category)
            }
        }
    }

    override suspend fun insertSports(sports: List<Sport>) {
        sportDao.insertSports(sports.map { sport -> sport.toEntity() })
    }
}