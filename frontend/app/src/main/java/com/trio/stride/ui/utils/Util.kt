package com.trio.stride.ui.utils

import com.google.gson.Gson
import okhttp3.ResponseBody
import java.text.DecimalFormat

fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun isValidPassword(password: String): Boolean {
    return password.length >= 6
}

data class ErrorResponse(
    val message: String,
    val status: String?,
)

fun parseErrorResponse(errorBody: ResponseBody?): ErrorResponse {
    return try {
        val errorJson = errorBody?.string()
        Gson().fromJson(errorJson, ErrorResponse::class.java)
    } catch (e: Exception) {
        ErrorResponse("Unknown error occurred", null)
    }
}

fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(minutes, secs)
}

fun formatTimeByMillis(millis: Long): String {
    val totalSeconds = millis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    val timeString = if (hours > 0) {
        "%d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%02d:%02d".format(minutes, seconds)
    }
    return timeString
}

fun formatDistance(distance: Double): String {
    val df = DecimalFormat("#.##")
    val formattedDistance = df.format(distance / 1000)
    return formattedDistance
}

fun formatSpeed(speed: Double): String {
    val df = DecimalFormat("#.#")
    val formattedSpeed = df.format(speed)
    return formattedSpeed
}