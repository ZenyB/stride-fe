package com.trio.stride.data.remote.dto

data class LoginRequestDto(
    val username: String,
    val password: String
)

data class AuthResponseDto(
    val token: String? = null,
    val expiryTime: Long? = null,
    val userIdentityId: String? = null
)

data class LoginGoogleRequestDto(
    val idToken: String
)

data class LogoutRequestDTO(
    val token: String?
)