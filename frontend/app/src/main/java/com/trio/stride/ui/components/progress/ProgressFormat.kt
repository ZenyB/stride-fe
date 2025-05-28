package com.trio.stride.ui.components.progress

import android.util.Log
import com.trio.stride.domain.model.Progress
import com.trio.stride.domain.model.ProgressTimeRange
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun getLastWeeksChartData(progresses: List<Progress>, weekCount: Int): List<Progress> {
    val calendar = Calendar.getInstance()
    calendar.firstDayOfWeek = Calendar.MONDAY
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    val chartData = mutableListOf<Progress>()

    repeat(weekCount) {
        val startOfWeek = calendar.timeInMillis

        val endCalendar = calendar.clone() as Calendar
        endCalendar.add(Calendar.WEEK_OF_YEAR, 1)
        val endOfWeek = endCalendar.timeInMillis - 1

        val progress = progresses.find {
            it.fromDate == startOfWeek && it.toDate == endOfWeek
        }

        chartData.add(
            Progress(
                distance = progress?.distance ?: 0.0,
                numberActivities = progress?.numberActivities ?: 0,
                fromDate = startOfWeek,
                toDate = endOfWeek,
                elevation = progress?.elevation ?: 0,
                time = progress?.time ?: 0,
            )
        )

        calendar.add(Calendar.WEEK_OF_YEAR, -1)
    }
    return chartData.reversed()
}

fun getLastDaysChartData(progresses: List<Progress>): List<Progress> {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    val chartData = mutableListOf<Progress>()

    repeat(7) {
        val startOfDay = calendar.timeInMillis

        val endCalendar = calendar.clone() as Calendar
        endCalendar.add(Calendar.DAY_OF_YEAR, 1)
        val endOfDay = endCalendar.timeInMillis - 1

        val progress = progresses.find {
            it.fromDate == startOfDay && it.toDate == endOfDay
        }

        chartData.add(
            Progress(
                distance = progress?.distance ?: 0.0,
                numberActivities = progress?.numberActivities ?: 0,
                fromDate = startOfDay,
                toDate = endOfDay,
                elevation = progress?.elevation ?: 0,
                time = progress?.time ?: 0,
            )
        )

        calendar.add(Calendar.DAY_OF_YEAR, -1)
    }

    return chartData.reversed()
}

fun getLastMonthChartData(progresses: List<Progress>): List<Progress> {
    val calendar = Calendar.getInstance()

    // Go back 30 days from today
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    calendar.add(Calendar.DAY_OF_MONTH, -29) // Today counts as one of the 30

    val chartData = mutableListOf<Progress>()

    repeat(30) {
        val startOfDay = calendar.timeInMillis

        val endCalendar = calendar.clone() as Calendar
        endCalendar.add(Calendar.DAY_OF_MONTH, 1)
        val endOfDay = endCalendar.timeInMillis - 1

        val progress = progresses.find {
            it.fromDate == startOfDay && it.toDate == endOfDay
        }

        chartData.add(
            Progress(
                distance = progress?.distance ?: 0.0,
                numberActivities = progress?.numberActivities ?: 0,
                fromDate = startOfDay,
                toDate = endOfDay,
                elevation = progress?.elevation ?: 0,
                time = progress?.time ?: 0,
            )
        )

        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }

    return chartData
}

fun getYearToCurrentWeekChartData(progresses: List<Progress>): List<Progress> {
    val now = Calendar.getInstance()

    // Set up current week boundary
    val currentWeek = now.get(Calendar.WEEK_OF_YEAR)
    val currentYear = now.get(Calendar.YEAR)

    // Start from the first week of the year
    val calendar = Calendar.getInstance()
    calendar.clear()
    calendar.set(Calendar.YEAR, currentYear)
    calendar.set(Calendar.WEEK_OF_YEAR, 1)
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    val chartData = mutableListOf<Progress>()

    for (week in 1..currentWeek) {
        val startOfWeek = calendar.timeInMillis

        val endCalendar = calendar.clone() as Calendar
        endCalendar.add(Calendar.WEEK_OF_YEAR, 1)
        val endOfWeek = endCalendar.timeInMillis - 1

        val progress = progresses.find {
            it.fromDate == startOfWeek && it.toDate == endOfWeek
        }

        chartData.add(
            Progress(
                distance = progress?.distance ?: 0.0,
                numberActivities = progress?.numberActivities ?: 0,
                fromDate = startOfWeek,
                toDate = endOfWeek,
                elevation = progress?.elevation ?: 0,
                time = progress?.time ?: 0,
            )
        )

        calendar.add(Calendar.WEEK_OF_YEAR, 1)
    }

    return chartData
}


val zeroWidthSpace = "\u200B"


fun getLast12WeeksLabels(chartData: List<Progress>, timeRange: ProgressTimeRange): List<String> {
    val monthFormatter = SimpleDateFormat("MMM", Locale.getDefault())
    val dayMonthFormatter = SimpleDateFormat("MMM d", Locale.getDefault())
    val dayFormatter = SimpleDateFormat("EEE", Locale.ENGLISH)
    val labels = mutableListOf<String>()

    for (progress in chartData) {
        val formatter = when (timeRange) {
            ProgressTimeRange.LAST_7_DAYS -> dayFormatter
            ProgressTimeRange.LAST_1_MONTH -> dayMonthFormatter
            else -> monthFormatter
        }
        val month = formatter.format(Date(progress.fromDate))
        labels.add(month)

    }
    Log.d("get weeks label", "chart data length: ${chartData.size}")

    Log.d("get weeks label", "lable length: ${labels.size}")
    Log.d("get weeks label", "lable list: ${labels}")
    return labels
}

fun formatWeekRange(fromDate: Long, toDate: Long): String {
    val now = Calendar.getInstance()
    now.firstDayOfWeek = Calendar.MONDAY

    // Start of this week
    val startOfWeek = now.clone() as Calendar
    startOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    startOfWeek.set(Calendar.HOUR_OF_DAY, 0)
    startOfWeek.set(Calendar.MINUTE, 0)
    startOfWeek.set(Calendar.SECOND, 0)
    startOfWeek.set(Calendar.MILLISECOND, 0)

    // End of this week: next Monday at 00:00:00.000 - 1 ms
    val endOfWeek = startOfWeek.clone() as Calendar
    endOfWeek.add(Calendar.WEEK_OF_YEAR, 1)
    endOfWeek.timeInMillis -= 1

    // Check if the provided range exactly matches this week
    if (fromDate == startOfWeek.timeInMillis && toDate == endOfWeek.timeInMillis) {
        return "This week"
    }

    val fromCal = Calendar.getInstance().apply { timeInMillis = fromDate }
    val toCal = Calendar.getInstance().apply { timeInMillis = toDate }

    // If it's the same day (ignore time)
    if (fromCal.get(Calendar.YEAR) == toCal.get(Calendar.YEAR) &&
        fromCal.get(Calendar.MONTH) == toCal.get(Calendar.MONTH) &&
        fromCal.get(Calendar.DAY_OF_MONTH) == toCal.get(Calendar.DAY_OF_MONTH)
    ) {
        val oneDayFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        return oneDayFormat.format(Date(fromDate))
    }

    // Format range
    val from = Date(fromDate)
    val to = Date(toDate)

    val fromMonth = SimpleDateFormat("MMM", Locale.getDefault()).format(from)
    val toMonth = SimpleDateFormat("MMM", Locale.getDefault()).format(to)

    return if (fromMonth == toMonth) {
        val dayFormat = SimpleDateFormat("MMM d", Locale.getDefault())
        val endFormat = SimpleDateFormat("d, yyyy", Locale.getDefault())
        "${dayFormat.format(from)} – ${endFormat.format(to)}"
    } else {
        val fullFormat = SimpleDateFormat("MMM d", Locale.getDefault())
        val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        "${fullFormat.format(from)} – ${fullFormat.format(to)}, ${yearFormat.format(to)}"
    }
}
