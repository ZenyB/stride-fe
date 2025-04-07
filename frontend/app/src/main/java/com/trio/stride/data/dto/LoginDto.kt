package com.trio.stride.data.dto

data class LoginRequestDto(
    val username: String,
    val password: String
)

data class AuthResponseDto(
    val token: String,
    val expiryTime: String
)