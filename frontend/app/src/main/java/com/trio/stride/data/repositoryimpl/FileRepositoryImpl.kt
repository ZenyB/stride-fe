package com.trio.stride.data.repositoryimpl

import com.trio.stride.data.remote.apiservice.file.FileApi
import com.trio.stride.domain.repository.FileRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class FileRepositoryImpl @Inject constructor(
    private val fileApi: FileApi
) : FileRepository {
    override suspend fun uploadFile(file: File): String {
        val requestBody = file.readBytes()
            .toRequestBody("image/*".toMediaTypeOrNull())

        val multipartBody = MultipartBody.Part.createFormData(
            name = "file",
            filename = file.name,
            body = requestBody
        )

        return fileApi.uploadFile(multipartBody).file
    }
}