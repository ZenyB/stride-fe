package com.trio.stride.domain.model

sealed class AuthInfo {
    data class WithToken(
        val token: String,
        val expiryTime: Long
    ) : AuthInfo()

    data class WithUserIdentity(
        val userIdentityId: String
    ) : AuthInfo()
}