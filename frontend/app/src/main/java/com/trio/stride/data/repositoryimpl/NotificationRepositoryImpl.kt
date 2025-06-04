package com.trio.stride.data.repositoryimpl

import com.trio.stride.data.local.dao.NotificationDao
import com.trio.stride.data.mapper.roomdatabase.toEntity
import com.trio.stride.data.mapper.roomdatabase.toModel
import com.trio.stride.data.remote.apiservice.notification.NotificationApi
import com.trio.stride.domain.model.Notification
import com.trio.stride.domain.model.NotificationItem
import com.trio.stride.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val notificationApi: NotificationApi,
    private val notificationDao: NotificationDao
) : NotificationRepository {
    override suspend fun getNotifications(page: Int?, limit: Int?): Notification {
        val response = notificationApi.getNotifications(page, limit)
        val items = response.data.map {
            NotificationItem(
                id = it.id,
                title = it.title,
                body = it.body,
                time = it.createdAt,
                seen = it.seen
            )
        }
        val paging = response.page

        return Notification(items, paging)
    }

    override suspend fun makeSeenAllNoti(): Boolean {
        return notificationApi.makeSeenNotifications().data
    }

    override suspend fun makeSeenNoti(id: String): Boolean {
        return notificationApi.makeSeenNotification(id).data
    }

    override suspend fun lcGetNotificationsPage1(): List<NotificationItem> {
        val localData = notificationDao.getNotifications().firstOrNull()
        return localData?.map { it.toModel() } ?: emptyList()
    }

    override suspend fun lcMakeSeenNotification(id: String) {
        notificationDao.makeSeenNotification(id)
    }

    override suspend fun lcMakeSeenNotifications() {
        notificationDao.makeSeenNotifications()
    }

    override suspend fun lcInsertNotificationsPage1(notifications: List<NotificationItem>) {
        notificationDao.deleteNotifications()
        notificationDao.insertNotifications(notifications.map { it.toEntity() })
    }
}