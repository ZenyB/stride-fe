package com.trio.stride.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.trio.stride.data.dto.AuthResponseDto
import com.trio.stride.domain.model.AuthInfo
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
fun AuthResponseDto.WithToken.toDomain(): AuthInfo {
    val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    val localDateTime = OffsetDateTime.parse(expiryTime, formatter).toLocalDateTime()

    return AuthInfo.WithToken(
        token = token,
        expiryTime = localDateTime
    )
}
