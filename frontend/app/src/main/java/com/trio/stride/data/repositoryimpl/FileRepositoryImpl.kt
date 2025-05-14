package com.trio.stride.data.repositoryimpl

import android.content.Context
import android.net.Uri
import com.trio.stride.data.remote.apiservice.file.FileApi
import com.trio.stride.domain.repository.FileRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class FileRepositoryImpl @Inject constructor(
    private val fileApi: FileApi
) : FileRepository {
    override suspend fun uploadFile(file: Uri, context: Context): String {
        val multipartBody = prepareFilePart(context, file, "file")

        return fileApi.uploadFile(multipartBody).file
    }

    private fun prepareFilePart(context: Context, uri: Uri, partName: String): MultipartBody.Part {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        val fileName = "image_${System.currentTimeMillis()}.jpg"

        val tempFile = File(context.cacheDir, fileName)
        inputStream?.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, tempFile.name, requestFile)
    }
}