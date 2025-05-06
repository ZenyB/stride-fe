package com.trio.stride.data.repositoryimpl

import com.trio.stride.data.apiservice.sport.SportApi
import com.trio.stride.domain.model.Category
import com.trio.stride.domain.model.Sport
import com.trio.stride.domain.repository.SportRepository
import javax.inject.Inject

class SportRepositoryImpl @Inject constructor(
    private val sportApi: SportApi
) : SportRepository {
    override suspend fun getSports(
        page: Int?,
        limit: Int?,
        name: String?,
        categoryId: String?
    ): List<Sport> {
        val result = sportApi.getSports(page, limit, name, categoryId)
        return result.map { response ->
            Sport(
                id = response.data.id,
                name = response.data.name,
                category = Category(response.data.category.id, response.data.category.name),
                image = response.data.image,
                sportMapType = response.data.sportMapType
            )
        }
    }
}