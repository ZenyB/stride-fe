package com.trio.stride.data.dto

data class SignUpRequest(
    val email: String,
    val password: String
)

data class SignUpResponse (
    val userIdentityId: String
)