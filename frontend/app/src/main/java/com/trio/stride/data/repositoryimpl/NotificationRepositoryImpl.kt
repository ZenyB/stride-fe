package com.trio.stride.data.repositoryimpl

import com.trio.stride.data.remote.apiservice.notification.NotificationApi
import com.trio.stride.domain.model.NotificationItem
import com.trio.stride.domain.repository.NotificationRepository
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val notificationApi: NotificationApi
) : NotificationRepository {
    override suspend fun getNotifications(page: Int?, limit: Int?): List<NotificationItem> {
        return notificationApi.getNotifications(page, limit).data.map {
            NotificationItem(
                id = it.id,
                title = it.title,
                body = it.body,
                time = it.createdAt,
                seen = it.seen
            )
        }
    }

    override suspend fun makeSeenAllNoti(): Boolean {
        return notificationApi.makeSeenNotifications().data
    }

    override suspend fun makeSeenNoti(id: String): Boolean {
        return notificationApi.makeSeenNotification(id).data
    }

}