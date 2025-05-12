package com.trio.stride.domain.usecase.auth

import com.trio.stride.base.NetworkException
import com.trio.stride.base.Resource
import com.trio.stride.base.UnknownException
import com.trio.stride.data.datastoremanager.SportManager
import com.trio.stride.data.datastoremanager.TokenManager
import com.trio.stride.data.local.dao.CurrentUserDao
import com.trio.stride.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    val repository: AuthRepository,
    private val tokenManager: TokenManager,
    private val currentUserDao: CurrentUserDao,
    private val sportManager: SportManager,
) {

    operator fun invoke(): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        try {
            val result = repository.logout(tokenManager.getAccessToken().first().toString())
            currentUserDao.deleteCurrentUser()
            tokenManager.clearTokens()
            sportManager.clearSportUserData()
            emit(Resource.Success(result))
        } catch (e: IOException) {
            emit(Resource.Error(NetworkException(e.message.toString())))
        } catch (e: Exception) {
            emit(Resource.Error(UnknownException(e.message.toString())))
        }
    }
}