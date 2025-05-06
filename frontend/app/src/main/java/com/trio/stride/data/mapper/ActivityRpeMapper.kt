package com.trio.stride.data.mapper

fun Int.toRpeString(): String {
    return when (this) {
        in (0..2) -> "Easy"
        in (3..5) -> "Moderate"
        in (6..9) -> "Hard"
        10 -> "Max Effort"
        else -> "Unknown"
    }
}