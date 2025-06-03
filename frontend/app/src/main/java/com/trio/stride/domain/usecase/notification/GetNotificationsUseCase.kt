package com.trio.stride.domain.usecase.notification

import com.trio.stride.base.NetworkException
import com.trio.stride.base.Resource
import com.trio.stride.domain.model.NotificationItem
import com.trio.stride.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(
        page: Int? = null,
        limit: Int? = null
    ): Flow<Resource<List<NotificationItem>>> = flow {
        emit(Resource.Loading())

//        val localData: List<NotificationItem>? = null

        try {
            val result = notificationRepository.getNotifications(page, limit)
            emit(Resource.Success(result))
        } catch (e: IOException) {
//            if (localData != null && localData.isNotEmpty()) {
            emit(Resource.Error(NetworkException(e.message ?: "IO Error")))
//            } else
//                emit(Resource.Success(localData))
        } catch (e: Exception) {
//            if (localData != null && localData.isNotEmpty()) {
            emit(
                Resource.Error(
                    com.trio.stride.base.UnknownException(
                        e.message ?: "Unknown Error"
                    )
                )
            )
//            } else emit(Resource.Success(localData))
        }
    }
}