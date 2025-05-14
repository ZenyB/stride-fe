package com.trio.stride.domain.usecase.category

import com.trio.stride.base.NetworkException
import com.trio.stride.base.Resource
import com.trio.stride.base.UnknownException
import com.trio.stride.domain.model.Category
import com.trio.stride.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class GetCategoriesUseCase(
    private val categoryRepository: CategoryRepository
) {

    operator fun invoke(
        page: Int? = null,
        limit: Int? = null,
        name: String? = null,
        forceRefresh: Boolean = false,
    ): Flow<Resource<List<Category>>> = flow {
        emit(Resource.Loading())

        val localData = categoryRepository.getLocalCategories()
        if (localData.isNotEmpty() && !forceRefresh) {
            emit(Resource.Success(localData))
            return@flow
        }

        try {
            val remoteData = categoryRepository.getCategories(page, limit, name)

            categoryRepository.insertCategories(remoteData)

            val updatedLocal = categoryRepository.getLocalCategories()
            emit(Resource.Success(updatedLocal))
        } catch (e: IOException) {
            emit(Resource.Error(NetworkException(e.message.toString())))
        } catch (e: Exception) {
            emit(Resource.Error(UnknownException(e.message.toString())))
        }
    }
}