package com.trio.stride.domain.usecase.profile

import com.trio.stride.base.NetworkException
import com.trio.stride.base.Resource
import com.trio.stride.data.mapper.roomdatabase.toCurrentUserEntity
import com.trio.stride.domain.model.UserInfo
import com.trio.stride.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class GetUserUseCase(val repository: UserRepository) {

    operator fun invoke(forceRefresh: Boolean = false): Flow<Resource<UserInfo>> = flow {
        val localData = repository.getCurrentUser()
        if (localData != null && !forceRefresh) {
            emit(Resource.Success(localData))
        } else {
            emit(Resource.Loading())
        }

        try {
            val remoteData = repository.getUser()
            repository.saveCurrentUser(remoteData.toCurrentUserEntity())
            emit(Resource.Success(remoteData))
        } catch (e: IOException) {
            if (localData == null) {
                emit(Resource.Error(NetworkException(e.message ?: "IO Error")))
            }
        } catch (e: Exception) {
            if (localData == null) {
                emit(
                    Resource.Error(
                        com.trio.stride.base.UnknownException(
                            e.message ?: "Unknown Error"
                        )
                    )
                )
            }
        }
    }
}