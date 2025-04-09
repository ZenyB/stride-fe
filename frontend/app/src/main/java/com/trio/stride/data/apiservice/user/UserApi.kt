package com.trio.stride.data.apiservice.user

import com.trio.stride.data.ApiConstants
import com.trio.stride.data.dto.AuthResponseDto
import com.trio.stride.data.dto.GetUserResponse
import com.trio.stride.data.dto.LoginGoogleRequestDto
import com.trio.stride.data.dto.LoginRequestDto
import com.trio.stride.data.dto.SuccessResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserApi {
    @POST(ApiConstants.LOGIN_END_POINTS)
    suspend fun login(@Body request: LoginRequestDto): AuthResponseDto

    @POST(ApiConstants.LOGIN_GOOGLE)
    suspend fun loginWithGoogle(@Body request: LoginGoogleRequestDto): AuthResponseDto

    @POST(ApiConstants.LOGOUT)
    suspend fun logout(): SuccessResponse

    @GET(ApiConstants.PROFILE)
    suspend fun getUser(): GetUserResponse
}