package com.trio.stride.domain.usecase.fcmnotification

import android.util.Log
import com.trio.stride.base.NetworkException
import com.trio.stride.base.Resource
import com.trio.stride.domain.repository.FCMNotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class SaveFCMTokenUseCase @Inject constructor(
    private val fcmNotificationRepository: FCMNotificationRepository
) {
    operator fun invoke(token: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        try {
            val result = fcmNotificationRepository.saveToken(token)
            Log.i("SEND_FCM_TOKEN_TO_SERVER", token)
            emit(Resource.Success(result))
        } catch (e: IOException) {
            Log.i("SEND_FCM_TOKEN_TO_SERVER_ERROR", e.message.toString())
            emit(Resource.Error(NetworkException(e.message.toString())))
        } catch (e: Exception) {
            Log.i("SEND_FCM_TOKEN_TO_SERVER_ERROR", e.message.toString())
            emit(Resource.Error(com.trio.stride.base.UnknownException(e.message.toString())))
        }
    }
}