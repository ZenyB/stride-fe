package com.trio.stride.domain.repository

import com.trio.stride.data.dto.UploadFileResponseDto
import java.io.File

interface FileRepository {
    suspend fun uploadFile(file: File): String
}