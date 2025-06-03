package com.trio.stride.domain.model

import java.time.LocalDate
import java.time.ZoneId

data class NotificationItem(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val time: Long = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()
        .toEpochMilli(),
    val seen: Boolean = false
)