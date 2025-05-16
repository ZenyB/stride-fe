package com.trio.stride.data.remote.apiservice.goal

import com.trio.stride.data.ApiConstants
import com.trio.stride.data.remote.dto.CreateGoalDTO
import com.trio.stride.data.remote.dto.CreateGoalResponse
import com.trio.stride.data.remote.dto.GoalListResponse
import com.trio.stride.data.remote.dto.SuccessResponse
import com.trio.stride.data.remote.dto.UpdateGoalRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface GoalApi {
    @POST(ApiConstants.GOALS)
    suspend fun createGoal(
        @Body request: CreateGoalDTO
    ): Response<CreateGoalResponse>

    @GET(ApiConstants.GOALS_LIST)
    suspend fun getUserGoals(
    ): Response<GoalListResponse>

    @DELETE("${ApiConstants.GOALS}/{id}")
    suspend fun deleteGoal(
        @Path("id") id: String
    ): Response<SuccessResponse>

    @PUT("${ApiConstants.GOALS}/{id}")
    suspend fun updateGoal(
        @Path("id") id: String,
        @Body requestDTO: UpdateGoalRequestDto
    ): Response<SuccessResponse>
}