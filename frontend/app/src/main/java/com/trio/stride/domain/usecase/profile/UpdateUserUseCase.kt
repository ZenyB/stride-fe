package com.trio.stride.domain.usecase.profile

import com.trio.stride.base.FalseResponseException
import com.trio.stride.base.NetworkException
import com.trio.stride.base.Resource
import com.trio.stride.base.SyncLocalDataFailed
import com.trio.stride.data.remote.dto.UpdateUserRequestDto
import com.trio.stride.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val syncUserUseCase: SyncUserUseCase
) {

    operator fun invoke(request: UpdateUserRequestDto): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        try {
            val result = userRepository.updateUser(request)

            if (result) {
                syncUserUseCase.invoke().collectLatest { response ->
                    when (response) {
                        is Resource.Error -> emit(Resource.Error(SyncLocalDataFailed("Update local user failed")))
                        is Resource.Success -> emit(Resource.Success(true))
                        else -> Unit
                    }
                }
            } else
                emit(Resource.Error(FalseResponseException("Update profile failed")))
        } catch (e: IOException) {
            emit(Resource.Error(NetworkException(e.message.toString())))
        } catch (e: Exception) {
            emit(Resource.Error(com.trio.stride.base.UnknownException(e.message.toString())))
        }
    }
}