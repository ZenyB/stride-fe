package com.trio.stride.data.remote.apiservice.fcmnotification

import com.trio.stride.data.ApiConstants
import com.trio.stride.data.remote.dto.SuccessResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface FCMNotificationApi {
    @POST(ApiConstants.FCM)
    suspend fun saveToken(
        @Body token: String
    ): SuccessResponse

    @DELETE("${ApiConstants.DELETE_FCM}/{token}")
    suspend fun deleteToken(
        @Path("token") token: String
    ): SuccessResponse
}