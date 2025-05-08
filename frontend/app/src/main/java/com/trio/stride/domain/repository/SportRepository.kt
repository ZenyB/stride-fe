package com.trio.stride.domain.repository

import com.trio.stride.domain.model.Sport

interface SportRepository {
    suspend fun getSports(
        page: Int? = null,
        limit: Int? = null,
        name: String? = null,
        categoryId: String? = null,
    ): List<Sport>

    suspend fun getLocalSports(categoryId: String?): List<Sport>
    suspend fun insertSports(sports: List<Sport>)
}