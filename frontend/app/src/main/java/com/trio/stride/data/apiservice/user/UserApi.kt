package com.trio.stride.data.apiservice.user

import com.trio.stride.data.ApiConstants
import com.trio.stride.data.dto.AuthResponseDto
import com.trio.stride.data.dto.LoginRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi {
    @POST(ApiConstants.LOGIN_END_POINTS)
    suspend fun login(@Body request: LoginRequestDto): AuthResponseDto
}