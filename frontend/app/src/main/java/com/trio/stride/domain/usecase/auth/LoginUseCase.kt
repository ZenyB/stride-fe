package com.trio.stride.domain.usecase.auth

import com.trio.stride.base.NetworkException
import com.trio.stride.base.NotFoundException
import com.trio.stride.base.UnauthorizedException
import com.trio.stride.base.UnknownException
import com.trio.stride.domain.model.AuthInfo
import com.trio.stride.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class LoginUseCase(private val repository: AuthRepository) {
    operator fun invoke(email: String, password: String): Flow<Resource<AuthInfo>> = flow {
        emit(Resource.Loading())

        try {
            val result = repository.login(email, password)
            when (result) {
                is AuthInfo.WithToken -> emit(Resource.Success(result))
                is AuthInfo.WithUserIdentity -> emit(Resource.Success(result))
            }
        } catch (e: HttpException) {
            when (e.code()) {
                403 -> emit(Resource.Error(UnauthorizedException(e.message.toString())))
                400 -> emit(Resource.Error(NotFoundException(e.message.toString())))
                else -> emit(Resource.Error(UnknownException(e.message.toString())))
            }
        } catch (e: IOException) {
            emit(Resource.Error(NetworkException(e.message.toString())))
        }
    }
}