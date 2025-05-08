package com.trio.stride.domain.repository

import java.io.File

interface FileRepository {
    suspend fun uploadFile(file: File): String
}