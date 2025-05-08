package com.trio.stride.data.remote.dto

data class VerifyOtpRequest(
    val token: String
)

data class VerifyOtpResponse(
    val data: Boolean
)