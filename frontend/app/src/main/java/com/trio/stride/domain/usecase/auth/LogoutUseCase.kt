package com.trio.stride.domain.usecase.auth

import android.os.Build
import androidx.annotation.RequiresApi
import com.trio.stride.base.NetworkException
import com.trio.stride.base.Resource
import com.trio.stride.base.UnknownException
import com.trio.stride.data.datastoremanager.TokenManager
import com.trio.stride.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    val repository: AuthRepository, private val tokenManager: TokenManager
) {

    @RequiresApi(Build.VERSION_CODES.O)
    operator fun invoke(): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        try {
            val result = repository.logout(tokenManager.getAccessToken().first().toString())
            emit(Resource.Success(result))
        } catch (e: IOException) {
            emit(Resource.Error(NetworkException(e.message.toString())))
        } catch (e: Exception) {
            emit(Resource.Error(UnknownException(e.message.toString())))
        }
    }
}