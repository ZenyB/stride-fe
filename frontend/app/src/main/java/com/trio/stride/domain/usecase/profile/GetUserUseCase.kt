package com.trio.stride.domain.usecase.profile

import com.trio.stride.base.NetworkException
import com.trio.stride.base.Resource
import com.trio.stride.domain.model.UserInfo
import com.trio.stride.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class GetUserUseCase(val repository: UserRepository) {

    operator fun invoke(): Flow<Resource<UserInfo>> = flow {
        emit(Resource.Loading())

        try {
            val result = repository.getUser()
            emit(Resource.Success(result))
        } catch (e: IOException) {
            emit(Resource.Error(NetworkException(e.message.toString())))
        } catch (e: Exception) {
            emit(Resource.Error(com.trio.stride.base.UnknownException(e.message.toString())))
        }
    }
}