package com.trio.stride.domain.usecase.fcmnotification

import com.trio.stride.base.NetworkException
import com.trio.stride.base.Resource
import com.trio.stride.domain.repository.FCMNotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class DeleteFCMTokenUseCase @Inject constructor(
    private val fcmNotificationRepository: FCMNotificationRepository
) {
    operator fun invoke(token: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        try {
            val result = fcmNotificationRepository.deleteToken(token)
            if (result) {
                fcmNotificationRepository.deleteLocalToken()
                emit(Resource.Success(true))
            }
        } catch (e: IOException) {
            emit(Resource.Error(NetworkException(e.message.toString())))
        } catch (e: Exception) {
            emit(Resource.Error(com.trio.stride.base.UnknownException(e.message.toString())))
        }
    }
}