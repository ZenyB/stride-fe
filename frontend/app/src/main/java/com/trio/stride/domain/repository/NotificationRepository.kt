package com.trio.stride.domain.repository

import com.trio.stride.domain.model.NotificationItem

interface NotificationRepository {
    suspend fun getNotifications(
        page: Int? = null,
        limit: Int? = null
    ): List<NotificationItem>

    suspend fun makeSeenAllNoti(): Boolean
    suspend fun makeSeenNoti(id: String): Boolean
}