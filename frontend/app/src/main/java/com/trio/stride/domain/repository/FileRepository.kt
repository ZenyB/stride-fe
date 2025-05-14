package com.trio.stride.domain.repository

import android.content.Context
import android.net.Uri

interface FileRepository {
    suspend fun uploadFile(file: Uri, context: Context): String
}