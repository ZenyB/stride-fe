package com.trio.stride.domain.usecase.auth

import com.trio.stride.base.IncorrectInfoException
import com.trio.stride.base.NetworkException
import com.trio.stride.base.Resource
import com.trio.stride.base.UnknownException
import com.trio.stride.data.datastoremanager.TokenManager
import com.trio.stride.domain.model.AuthInfo
import com.trio.stride.domain.repository.AuthRepository
import com.trio.stride.domain.usecase.fcmnotification.RefreshAndSaveFCMTokenUseCase
import com.trio.stride.domain.usecase.profile.SyncUserUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class LoginUseCase(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager,
    private val refreshAndSaveFCMTokenUseCase: RefreshAndSaveFCMTokenUseCase,
    private val syncUserUseCase: SyncUserUseCase
) {
    operator fun invoke(email: String, password: String): Flow<Resource<AuthInfo>> = flow {
        emit(Resource.Loading())

        try {
            when (val result = repository.login(email, password)) {
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
        } catch (e: HttpException) {
            when (e.code()) {
                400 -> emit(Resource.Error(IncorrectInfoException(e.message.toString())))
                else -> emit(Resource.Error(UnknownException(e.message.toString())))
            }
        } catch (e: IOException) {
            emit(Resource.Error(NetworkException(e.message.toString())))
        } catch (e: CancellationException) {
            throw e
        }
    }
}