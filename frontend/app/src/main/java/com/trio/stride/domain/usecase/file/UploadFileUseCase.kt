package com.trio.stride.domain.usecase.file

import android.content.Context
import android.net.Uri
import com.trio.stride.base.NetworkException
import com.trio.stride.base.Resource
import com.trio.stride.base.UnknownException
import com.trio.stride.domain.repository.FileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class UploadFileUseCase @Inject constructor(
    private val fileRepository: FileRepository
) {
    operator fun invoke(file: Uri, context: Context): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        val result = fileRepository.uploadFile(file, context)
        emit(Resource.Success(result))
    }.catch { e ->
        when (e) {
            is IOException -> emit(Resource.Error(NetworkException(e.message.toString())))
            else -> emit(Resource.Error(UnknownException(e.message.toString())))
        }
    }
}