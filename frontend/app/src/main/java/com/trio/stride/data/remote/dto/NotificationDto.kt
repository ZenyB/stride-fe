package com.trio.stride.data.remote.dto

data class NotificationResponseDto(
    val data: List<NotificationItemDto>,
    val page: PageDto
)

data class NotificationItemDto(
    val id: String,
    val title: String,
    val body: String,
    val createdAt: Long,
    val seen: Boolean
)