package com.trio.stride.data.remote.apiservice.notification

import com.trio.stride.data.ApiConstants
import com.trio.stride.data.remote.dto.NotificationResponseDto
import com.trio.stride.data.remote.dto.SuccessResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationApi {
    @GET(ApiConstants.NOTIFICATIONS)
    suspend fun getNotifications(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): NotificationResponseDto

    @POST(ApiConstants.MAKE_SEEN_NOTIFICATION)
    suspend fun makeSeenNotifications(): SuccessResponse

    @POST("${ApiConstants.MAKE_SEEN_NOTIFICATION}/{id}")
    suspend fun makeSeenNotification(
        @Path("id") id: String
    ): SuccessResponse
}