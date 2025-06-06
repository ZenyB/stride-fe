package com.trio.stride.domain.usecase.fcmnotification

import android.util.Log
import com.trio.stride.base.FalseResponseException
import com.trio.stride.base.NetworkException
import com.trio.stride.base.Resource
import com.trio.stride.base.UnknownException
import com.trio.stride.domain.repository.FCMNotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class SaveFCMTokenUseCase @Inject constructor(
    private val fcmNotificationRepository: FCMNotificationRepository
) {
    operator fun invoke(token: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        try {
            val result = fcmNotificationRepository.saveToken(token)
            if (result) {
                Log.i("SEND_FCM_TOKEN_TO_SERVER", token)
                fcmNotificationRepository.setIsTokenSynced(true)
                fcmNotificationRepository.addTokenToDelete(token)
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error(FalseResponseException("Failed to sent fcm token")))
            }
        } catch (e: HttpException) {
            if (e.code() == 409) {
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error(UnknownException("Http error")))
            }
        } catch (e: IOException) {
            Log.i("SEND_FCM_TOKEN_TO_SERVER_ERROR", e.message.toString())
            emit(Resource.Error(NetworkException(e.message.toString())))
        } catch (e: Exception) {
            Log.i("SEND_FCM_TOKEN_TO_SERVER_ERROR", e.message.toString())
            emit(Resource.Error(UnknownException(e.message.toString())))
        }
    }
}