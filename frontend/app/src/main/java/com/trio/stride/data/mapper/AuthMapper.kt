package com.trio.stride.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.trio.stride.data.dto.AuthResponseDto
import com.trio.stride.domain.model.AuthInfo
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
fun AuthResponseDto.toDomain(): AuthInfo {
    return if (token != null) {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val localDateTime = OffsetDateTime.parse(expiryTime, formatter).toLocalDateTime()
        AuthInfo.WithToken(token, localDateTime)
    } else {
        AuthInfo.WithUserIdentity(userIdentityId ?: "")
    }
}
