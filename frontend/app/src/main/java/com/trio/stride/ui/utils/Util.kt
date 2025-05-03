package com.trio.stride.ui.utils

import com.google.gson.Gson
import okhttp3.ResponseBody
import java.text.DecimalFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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

fun formatKmDistance(distance: Double): String {
    val df = DecimalFormat("#.##")
    return df.format(distance)
}

fun formatSpeed(speed: Double): String {
    val df = DecimalFormat("#.#")
    val formattedSpeed = df.format(speed)
    return formattedSpeed
}

fun formatDuration(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60

    return buildString {
        if (hours > 0) append("${hours}h")
        if (minutes > 0) append("${minutes}m")
        if (hours == 0L && minutes == 0L || secs > 0) append("${secs}s")
    }
}

fun formatDate(timestamp: Long): String {
    val zoneId = ZoneId.systemDefault()
    val now = LocalDate.now(zoneId)
    val dateTime = Instant.ofEpochMilli(timestamp).atZone(zoneId)
    val date = dateTime.toLocalDate()

    val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
    val fullDateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")

    return when {
        date.isEqual(now) -> "Today at ${dateTime.format(timeFormatter)}"
        date.isEqual(now.minusDays(1)) -> "Yesterday at ${dateTime.format(timeFormatter)}"
        else -> "${dateTime.format(fullDateFormatter)} at ${dateTime.format(timeFormatter)}"
    }
}