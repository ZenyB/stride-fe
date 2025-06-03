package com.trio.stride.domain.usecase.auth

import com.trio.stride.base.FalseResponseException
import com.trio.stride.base.NetworkException
import com.trio.stride.base.Resource
import com.trio.stride.base.UnknownException
import com.trio.stride.data.datastoremanager.FCMTokenManager
import com.trio.stride.data.datastoremanager.SportManager
import com.trio.stride.data.datastoremanager.TokenManager
import com.trio.stride.data.local.dao.CurrentUserDao
import com.trio.stride.domain.repository.AuthRepository
import com.trio.stride.domain.usecase.fcmnotification.DeleteFCMTokenUseCase
import com.trio.stride.domain.usecase.profile.ClearLocalUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    val repository: AuthRepository,
    private val tokenManager: TokenManager,
    private val currentUserDao: CurrentUserDao,
    private val sportManager: SportManager,
    private val fcmTokenManager: FCMTokenManager,
    private val clearCurrentUserUseCase: ClearLocalUserUseCase,
    private val deleteFCMTokenUseCase: DeleteFCMTokenUseCase
) {

    operator fun invoke(): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        try {
            val result = repository.logout(tokenManager.getAccessToken().first().toString())
            if (result) {
                currentUserDao.deleteCurrentUser()
                tokenManager.clearTokens()
                sportManager.clearSportUserData()
                fcmTokenManager.deleteToken()
                withContext(Dispatchers.IO) {
                    clearCurrentUserUseCase()
                    val oldToken = fcmTokenManager.getToken().firstOrNull()
                    if (oldToken != null)
                        deleteFCMTokenUseCase.invoke(oldToken)
                }
                emit(Resource.Success(true))
            } else
                emit(Resource.Error(FalseResponseException("Logout failed")))
        } catch (e: IOException) {
            emit(Resource.Error(NetworkException(e.message.toString())))
        } catch (e: Exception) {
            emit(Resource.Error(UnknownException(e.message.toString())))
        }
    }
}