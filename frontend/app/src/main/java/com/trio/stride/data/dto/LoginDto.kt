package com.trio.stride.data.dto

data class LoginRequestDto(
    val username: String,
    val password: String
)

sealed class AuthResponseDto {
    data class WithToken(
        val token: String,
        val expiryTime: String
    ) : AuthResponseDto()

    data class WithUserIdentity(
        val userIdentityId: String
    ) : AuthResponseDto()
}