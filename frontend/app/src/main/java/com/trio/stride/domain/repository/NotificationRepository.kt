package com.trio.stride.domain.repository

import com.trio.stride.domain.model.Notification
import com.trio.stride.domain.model.NotificationItem

interface NotificationRepository {
    suspend fun getNotifications(
        page: Int? = null,
        limit: Int? = null
    ): Notification

    suspend fun makeSeenAllNoti(): Boolean
    suspend fun makeSeenNoti(id: String): Boolean

    suspend fun lcGetNotificationsPage1(): List<NotificationItem>
    suspend fun lcMakeSeenNotification(id: String)
    suspend fun lcMakeSeenNotifications()
    suspend fun lcInsertNotificationsPage1(notifications: List<NotificationItem>)
}