package com.trio.stride.data.mapper.roomdatabase

import com.trio.stride.data.local.entity.NotificationEntity
import com.trio.stride.domain.model.NotificationItem

fun NotificationEntity.toModel(): NotificationItem = NotificationItem(
    id = this.id,
    title = this.title,
    body = this.body,
    time = this.time,
    seen = this.seen
)

fun NotificationItem.toEntity(): NotificationEntity = NotificationEntity(
    id = this.id,
    title = this.title,
    body = this.body,
    time = this.time,
    seen = this.seen
)