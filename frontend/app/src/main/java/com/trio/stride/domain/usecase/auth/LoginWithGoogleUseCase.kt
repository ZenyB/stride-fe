package com.trio.stride.domain.usecase.auth

import com.trio.stride.base.NetworkException
import com.trio.stride.base.Resource
import com.trio.stride.base.UnknownException
import com.trio.stride.data.datastoremanager.TokenManager
import com.trio.stride.domain.model.AuthInfo
import com.trio.stride.domain.repository.AuthRepository
import com.trio.stride.domain.usecase.fcmnotification.RefreshAndSaveFCMTokenUseCase
import com.trio.stride.domain.usecase.profile.SyncUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.IOException

class LoginWithGoogleUseCase(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager,
    private val refreshAndSaveFCMTokenUseCase: RefreshAndSaveFCMTokenUseCase,
    private val syncUserUseCase: SyncUserUseCase
) {

    operator fun invoke(idToken: String): Flow<Resource<AuthInfo>> = flow {
        emit(Resource.Loading())

        try {
            when (val result = repository.loginWithGoogle(idToken)) {
                is AuthInfo.WithToken -> {
                    tokenManager.saveAccessToken(result.token, result.expiryTime)
                    withContext(Dispatchers.IO) {
                        syncUserUseCase()
                        refreshAndSaveFCMTokenUseCase()
                    }
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