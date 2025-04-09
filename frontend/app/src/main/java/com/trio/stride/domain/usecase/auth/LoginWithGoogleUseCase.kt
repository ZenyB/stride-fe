package com.trio.stride.domain.usecase.auth

import com.trio.stride.base.NetworkException
import com.trio.stride.base.Resource
import com.trio.stride.base.UnknownException
import com.trio.stride.data.datastoremanager.TokenManager
import com.trio.stride.domain.model.AuthInfo
import com.trio.stride.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class LoginWithGoogleUseCase(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager
) {

    operator fun invoke(idToken: String): Flow<Resource<AuthInfo>> = flow {
        emit(Resource.Loading())

        try {
            val result = repository.loginWithGoogle(idToken)
            when (result) {
                is AuthInfo.WithToken -> {
                    tokenManager.saveAccessToken(result.token)
                    emit(Resource.Success(result))
                }

                is AuthInfo.WithUserIdentity -> emit(Resource.Success(result))
            }
        } catch (e: IOException) {
            emit(Resource.Error(NetworkException(e.message.toString())))
        } catch (e: Exception) {
            emit(Resource.Error(UnknownException(e.message.toString())))
        }
    }
}