package com.trio.stride.domain.model

import java.time.LocalDateTime

data class AuthInfo(
    val token: String,
    val expiryTime: LocalDateTime
)