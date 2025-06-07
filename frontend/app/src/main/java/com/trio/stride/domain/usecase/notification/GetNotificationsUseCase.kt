package com.trio.stride.domain.usecase.notification

import android.util.Log
import com.trio.stride.base.NetworkException
import com.trio.stride.base.Resource
import com.trio.stride.data.remote.dto.PageDto
import com.trio.stride.domain.model.Notification
import com.trio.stride.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
) {
    operator fun invoke(
        page: Int? = null,
        limit: Int? = null,
        forceRefresh: Boolean = false
    ): Flow<Resource<Notification>> = flow {
        emit(Resource.Loading())

        val localData = notificationRepository.lcGetNotificationsPage1()
        Log.i("GET_NOTI_LOCAL", "$page - $localData")
        if (localData.isNotEmpty() && !forceRefresh && page == 1) {
            emit(Resource.Success(Notification(localData, PageDto())))
        }

        try {
            val result = notificationRepository.getNotifications(page, limit)
            if (page == 1)
                notificationRepository.lcInsertNotificationsPage1(result.notificationItems)
            emit(Resource.Success(result))
        } catch (e: IOException) {
            Log.i("GET_NOTI_ERROR_1", e.message.toString())
            if (localData.isEmpty()) {
                emit(Resource.Error(NetworkException(e.message ?: "IO Error")))
            } else
                emit(Resource.Success(Notification(localData, PageDto())))
        } catch (e: Exception) {
            Log.i("GET_NOTI_ERROR", e.message.toString())
            if (localData.isEmpty()) {
                emit(
                    Resource.Error(
                        com.trio.stride.base.UnknownException(
                            e.message ?: "Unknown Error"
                        )
                    )
                )
            } else emit(Resource.Success(Notification(localData, PageDto())))
        }
    }
}