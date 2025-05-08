package com.trio.stride.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.trio.stride.data.remote.dto.AuthResponseDto
import com.trio.stride.domain.model.AuthInfo

@RequiresApi(Build.VERSION_CODES.O)
fun AuthResponseDto.toDomain(): AuthInfo {
    return if (token != null && expiryTime != null) {
        AuthInfo.WithToken(token, expiryTime)
    } else {
        AuthInfo.WithUserIdentity(userIdentityId ?: "")
    }
}
