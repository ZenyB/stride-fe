package com.trio.stride.domain.model

sealed class AuthInfo {
    data class WithToken(
        val token: String,
        val expiryTime: String
    ) : AuthInfo()

    data class WithUserIdentity(
        val userIdentityId: String
    ) : AuthInfo()
}