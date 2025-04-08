package com.trio.stride.ui.utils

import com.google.gson.Gson
import okhttp3.ResponseBody

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