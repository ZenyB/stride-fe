package com.trio.stride.data.dto

data class VerifyOtpRequest(
    val token: String
)

data class VerifyOtpResponse(
    val data: Boolean
)