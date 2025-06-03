package com.trio.stride.ui.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.google.gson.Gson
import okhttp3.ResponseBody
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Calendar
import java.util.Date
import java.util.Locale

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

// region: Date&Time fun
val zoneId: ZoneId = ZoneId.of("Asia/Ho_Chi_Minh")!!
val systemZoneId: ZoneId = ZoneId.systemDefault()

fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(minutes, secs)
}

fun formatTimeHM(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60

    return if (hours == 0 && minutes == 0)
        "%dh".format(hours, minutes)
    else
        "%dh %dm".format(hours, minutes)
}

fun formatTimeHMS(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = (seconds % 3600) % 60

    return if (hours == 0) {
        if (minutes == 0)
            "%ds".format(secs)
        else
            "%dm %ds".format(minutes, secs)
    } else
        "%dh %dm %ds".format(hours, minutes, secs)
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

fun formatDuration(seconds: Long, showSeconds: Boolean = true): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60

    return buildString {
        if (hours > 0) append("${hours}h")
        if (minutes > 0) append("${minutes}m")
        if (showSeconds && (hours == 0L && minutes == 0L || secs > 0)) append("${secs}s")
    }
}

fun formatDate(timestamp: Long): String {
    val now = LocalDate.now(systemZoneId)
    val dateTime = Instant.ofEpochMilli(timestamp).atZone(systemZoneId)
    val date = dateTime.toLocalDate()

    val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
    val fullDateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")

    return when {
        date.isEqual(now) -> "Today at ${dateTime.format(timeFormatter)}"
        date.isEqual(now.minusDays(1)) -> "Yesterday at ${dateTime.format(timeFormatter)}"
        else -> "${dateTime.format(fullDateFormatter)} at ${dateTime.format(timeFormatter)}"
    }
}

fun formatTimeWithDateTimestamp(timestamp: Long): String {
    val dateTime = Instant.ofEpochMilli(timestamp).atZone(systemZoneId)
    val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
    return dateTime.format(timeFormatter)
}

fun LocalDateTime.toDateString(): String = this.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
fun String.toDate(): LocalDate {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    return try {
        LocalDate.parse(this, formatter)
    } catch (e: Exception) {
        LocalDate.now()
    }
}

fun getStartOf12WeeksInMillis(): Long {
    val now = LocalDate.now()

    val startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    val start12Weeks = startOfWeek.minusWeeks(11)
        .atStartOfDay(systemZoneId)
        .toInstant()
        .toEpochMilli()

    return start12Weeks
}

fun getEndOfWeekInMillis(ofDate: Long? = null): Long {
    val date = if (ofDate != null) Instant.ofEpochMilli(ofDate)
        .atZone(systemZoneId)
        .toLocalDate() else LocalDate.now()

    val endOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
        .atTime(LocalTime.MAX)
        .atZone(systemZoneId)
        .toInstant()
        .toEpochMilli()

    return endOfWeek
}

fun getStartOfWeekInMillis(ofDate: Long? = null): Long {
    val date = if (ofDate != null) Instant.ofEpochMilli(ofDate)
        .atZone(systemZoneId)
        .toLocalDate()
    else LocalDate.now()

    val startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        .atTime(LocalTime.MIN)
        .atZone(systemZoneId)
        .toInstant()
        .toEpochMilli()

    return startOfWeek
}

fun Long.minus12Weeks(): Long {
    val twelveWeeksInMillis = 12L * 7 * 24 * 60 * 60 * 1000

    val newMillis = this - twelveWeeksInMillis

    return newMillis
}

fun Long.minusNWeeks(n: Int): Long {
    val nWeeksInMillis = n * 7L * 24 * 60 * 60 * 1000

    val newMillis = this - nWeeksInMillis

    return newMillis
}

fun Pair<Long, Long>.toStringDateRange(): String {
    val startDate = Date(first)
    val endDate = Date(second)

    val dayFormat = SimpleDateFormat("d", Locale.ENGLISH)
    val monthFormat =
        SimpleDateFormat("MMM", Locale.ENGLISH)
    val yearFormat =
        SimpleDateFormat("yyyy", Locale.ENGLISH)

    val startDay = dayFormat.format(startDate)
    val endDay = dayFormat.format(endDate)
    val startMonth = monthFormat.format(startDate)
    val endMonth = monthFormat.format(endDate)
    val startYear = yearFormat.format(startDate)
    val endYear = yearFormat.format(endDate)

    return if (startYear == endYear) {
        if (startMonth == endMonth)
            "$startDay - $endDay $endMonth $startYear".uppercase()
        else
            "$startDay $startMonth - $endDay $endMonth $startYear".uppercase()
    } else {
        "$startDay $startMonth $startYear - $endDay $endMonth $endYear".uppercase()
    }
}

fun Long.compareHCMDateWithSystemDate(otherTimestamp: Long): Int {
    val thisDate = Instant.ofEpochMilli(this)
        .atZone(zoneId)
        .toLocalDate()

    val otherDate = Instant.ofEpochMilli(otherTimestamp)
        .atZone(systemZoneId)
        .toLocalDate()

    return thisDate.compareTo(otherDate)
}

fun Long.toStringDate(): String {
    val date = Date(this)
    val dateFormat = SimpleDateFormat("d MMM yyyy", Locale.ENGLISH)
    return dateFormat.format(date)
}

fun Long.toTimeAgo(): String {
    val now = System.currentTimeMillis()
    val diff = now - this

    val seconds = diff / 1000
    val minutes = diff / (1000 * 60)
    val hours = diff / (1000 * 60 * 60)
    val days = diff / (1000 * 60 * 60 * 24)

    return when {
        seconds < 60 -> "just now"
        minutes < 60 -> "$minutes minute${if (minutes == 1L) "" else "s"} ago"
        hours < 24 -> "$hours hour${if (hours == 1L) "" else "s"} ago"
        days < 30 -> "$days day${if (days == 1L) "" else "s"} ago"
        else -> {
            val date = Date(this)
            val calendar = Calendar.getInstance()
            val currentYear = calendar.get(Calendar.YEAR)

            calendar.time = date
            val year = calendar.get(Calendar.YEAR)

            if (year == currentYear) {
                SimpleDateFormat("dd MMMM", Locale.getDefault()).format(date)
            } else {
                SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(date)
            }
        }
    }
}

// endregion

// region: format activity values
fun formatDistance(distance: Double): String {
    val df = DecimalFormat("#.##")
    val formattedDistance = df.format(distance / 1000.0)
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

// endregion

fun calculateContrast(bg: Color, fg: Color): Float {
    val l1 = bg.luminance() + 0.05f
    val l2 = fg.luminance() + 0.05f
    return if (l1 > l2) l1 / l2 else l2 / l1
}

fun Color.contrastingTextColor(): Color {
    val whiteContrast = calculateContrast(this, Color.White)
    val blackContrast = calculateContrast(this, Color.Black)
    return if (whiteContrast >= blackContrast) Color.White else Color.Black
}

fun Boolean.toGender(): String = if (this) "Male" else "Female"
fun String.toBoolGender(): Boolean = this != "Female"

fun LocalDate.isValidBirthDay(): Boolean {
    val today = LocalDate.now()
    val age = Period.between(this, today).years
    return age >= 10
}