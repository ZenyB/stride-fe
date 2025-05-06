package com.trio.stride.domain.usecase.sport

import com.trio.stride.base.NetworkException
import com.trio.stride.base.Resource
import com.trio.stride.base.UnknownException
import com.trio.stride.domain.model.Sport
import com.trio.stride.domain.repository.SportRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class GetSportsUseCase(
    private val sportRepository: SportRepository
) {

    operator fun invoke(
        page: Int? = null,
        limit: Int? = null,
        name: String? = null,
        categoryId: String? = null
    ): Flow<Resource<List<Sport>>> = flow {
        emit(Resource.Loading())

        try {
            val result = sportRepository.getSports(page, limit, name, categoryId)
            emit(Resource.Success(result))
        } catch (e: IOException) {
            emit(Resource.Error(NetworkException(e.message.toString())))
        } catch (e: Exception) {
            emit(Resource.Error(UnknownException(e.message.toString())))
        }
    }
}