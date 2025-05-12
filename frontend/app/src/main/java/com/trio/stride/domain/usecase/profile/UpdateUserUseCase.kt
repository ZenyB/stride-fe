package com.trio.stride.domain.usecase.profile

import com.trio.stride.base.NetworkException
import com.trio.stride.base.Resource
import com.trio.stride.data.remote.dto.UpdateUserRequestDto
import com.trio.stride.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
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

            syncUserUseCase.invoke()

            emit(Resource.Success(result))
        } catch (e: IOException) {
            emit(Resource.Error(NetworkException(e.message.toString())))
        } catch (e: Exception) {
            emit(Resource.Error(com.trio.stride.base.UnknownException(e.message.toString())))
        }
    }
}