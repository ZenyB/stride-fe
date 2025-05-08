package com.trio.stride.data.remote.apiservice.file

import com.trio.stride.data.ApiConstants
import com.trio.stride.data.remote.dto.UploadFileResponseDto
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface FileApi {
    @Multipart
    @POST(ApiConstants.FILE)
    suspend fun uploadFile(
        @Part file: MultipartBody.Part,
    ): UploadFileResponseDto
}