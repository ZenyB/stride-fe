package com.trio.stride.data.dto

data class LoginRequestDto(
    val username: String,
    val password: String
)

data class AuthResponseDto(
    val token: String? = null,
    val expiryTime: String? = null,
    val userIdentityId: String? = null
)

data class LoginGoogleRequestDto(
    val idToken: String
)

data class LogoutRequestDTO(
    val token: String?
)