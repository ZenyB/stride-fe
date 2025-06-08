package com.trio.stride.domain.usecase.profile

import com.trio.stride.base.NetworkException
import com.trio.stride.base.Resource
import com.trio.stride.data.mapper.roomdatabase.toCurrentUserEntity
import com.trio.stride.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class SyncUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val remoteData = repository.getUser()
            withContext(Dispatchers.IO) {
                repository.saveCurrentUser(remoteData.toCurrentUserEntity())
            }
            emit(Resource.Success(Unit))
        } catch (e: IOException) {
            emit(Resource.Error(NetworkException(e.message.toString())))
        } catch (e: Exception) {
            emit(Resource.Error(com.trio.stride.base.UnknownException(e.message.toString())))
        }

    }
}