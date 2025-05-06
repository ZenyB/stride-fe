package com.trio.stride.data.mapper

import com.trio.stride.domain.model.SportMapType

fun String.toSportMapType(): SportMapType {
    return when (this.uppercase()) {
        "WALKING" -> SportMapType.WALKING
        "DRIVING" -> SportMapType.DRIVING
        "CYCLING" -> SportMapType.CYCLING
        else -> SportMapType.WALKING
    }
}