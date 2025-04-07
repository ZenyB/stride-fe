package com.trio.stride.domain.model

import java.time.LocalDateTime

sealed class AuthInfo {
    data class WithToken(
        val token: String,
        val expiryTime: LocalDateTime
    ) : AuthInfo()

    data class WithUserIdentity(
        val userIdentityId: String
    ) : AuthInfo()
}