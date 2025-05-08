package com.trio.stride.data.remote.dto

data class SendOTPResetPasswordRequest(
    val username: String
)

data class ResetPasswordVerifyRequest(
    val username: String,
    val token: String
)

data class ResetPasswordVerifyResponse(
    val resetPasswordId: String
)

data class ChangePasswordRequest(
    val token: String,
    val password: String,
)