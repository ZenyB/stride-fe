package com.trio.stride.data.remote.apiservice.user

import com.trio.stride.data.ApiConstants
import com.trio.stride.data.remote.dto.AuthResponseDto
import com.trio.stride.data.remote.dto.GetUserResponse
import com.trio.stride.data.remote.dto.LoginGoogleRequestDto
import com.trio.stride.data.remote.dto.LoginRequestDto
import com.trio.stride.data.remote.dto.LogoutRequestDTO
import com.trio.stride.data.remote.dto.SuccessResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserApi {
    @POST(ApiConstants.LOGIN_END_POINTS)
    suspend fun login(@Body request: LoginRequestDto): AuthResponseDto

    @POST(ApiConstants.LOGIN_GOOGLE)
    suspend fun loginWithGoogle(@Body request: LoginGoogleRequestDto): AuthResponseDto

    @POST(ApiConstants.LOGOUT)
    suspend fun logout(@Body request: LogoutRequestDTO): SuccessResponse

    @GET(ApiConstants.PROFILE)
    suspend fun getUser(): GetUserResponse
}