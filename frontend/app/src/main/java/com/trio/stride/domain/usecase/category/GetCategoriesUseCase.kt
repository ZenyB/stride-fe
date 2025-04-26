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
    val categoryRepository: CategoryRepository
) {

    operator fun invoke(
        page: Int? = null,
        limit: Int? = null,
        name: String? = null,
    ): Flow<Resource<List<Category>>> = flow {
        emit(Resource.Loading())

        try {
            val result = categoryRepository.getCategories(page, limit, name)
            emit(Resource.Success(result))
        } catch (e: IOException) {
            emit(Resource.Error(NetworkException(e.message.toString())))
        } catch (e: Exception) {
            emit(Resource.Error(UnknownException(e.message.toString())))
        }
    }
}