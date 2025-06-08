package com.trio.stride.data.repositoryimpl

import com.trio.stride.data.local.dao.SportDao
import com.trio.stride.data.mapper.roomdatabase.toEntity
import com.trio.stride.data.mapper.roomdatabase.toModel
import com.trio.stride.data.remote.apiservice.sport.SportApi
import com.trio.stride.domain.model.Sport
import com.trio.stride.domain.repository.SportRepository
import javax.inject.Inject

class SportRepositoryImpl @Inject constructor(
    private val sportApi: SportApi,
    private val sportDao: SportDao,
) : SportRepository {
    override suspend fun getSports(
        name: String?,
        categoryId: String?,
    ): List<Sport> {
        val result = sportApi.getSports(name, categoryId)
        return result.data.map { response ->
            Sport(
                id = response.id,
                name = response.name,
                categoryName = response.category?.name ?: "Foot sports",
                image = response.image,
                color = response.color,
                sportMapType = response.sportMapType
            )
        }
    }

    override suspend fun getLocalSports(categoryId: String?): List<Sport> {
        return sportDao.getAllSports().map { sportEntity ->
            sportEntity.toModel()
        }
    }

    override suspend fun insertSports(sports: List<Sport>) {
        sportDao.deleteSports()
        sportDao.insertSports(sports.map { sport -> sport.toEntity() })
    }
}