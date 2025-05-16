package com.trio.stride.domain.model

import com.trio.stride.ui.utils.formatDuration
import com.trio.stride.ui.utils.formatKmDistance
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class GoalType { ACTIVITY, DISTANCE, TIME, ELEVATION }

enum class GoalTimeFrame { WEEKLY, MONTHLY, ANNUALLY }

data class GoalItem(
    val id: String,
    val sport: Sport,
    val type: String,
    val timeFrame: String,
    val amountGain: Long,
    val amountGoal: Long,
    val isActive: Boolean,
    val histories: List<GoalHistoryItem>?,
)

data class GoalHistoryItem(
    val date: Long,
    val amountGain: Long,
    val amountGoal: Long,
)

data class GoalEdit(
    val id: String,
    val type: String,
    val timeFrame: String,
    val amount: Int
)

fun GoalItem.toTitle(): String {
    val title = "$type $timeFrame"
        .split(" ")
        .joinToString(" ") { it.lowercase(Locale.ROOT).replaceFirstChar { c -> c.uppercaseChar() } }
    return "$title Goal"
}

fun GoalItem.formatAmount(amount: Number): String {
    if (type == GoalType.TIME.name) {
        val result = formatDuration(amount.toLong(), false)
        if (result.isEmpty()) {
            return "0m"
        } else return result
    } else if (type == GoalType.DISTANCE.name) {
        return "${formatKmDistance(amount.toDouble())} km"
    } else if (type == GoalType.ELEVATION.name) {
        return "$amount m"
    } else {
        return amount.toString()
    }
}

fun GoalItem.toMonthLabels(): List<String> {
    val monthFormatter = SimpleDateFormat("MMM", Locale("en", "VN"))
    val calendar = Calendar.getInstance()

    val seenMonths = mutableSetOf<Int>() // Use Calendar.MONTH values (0 = Jan, 1 = Feb, ...)

    val labels: List<String> = histories?.map { item ->
        calendar.timeInMillis = item.date
        val month = calendar.get(Calendar.MONTH)

        if (month !in seenMonths) {
            seenMonths.add(month)
            monthFormatter.format(Date(item.date))
        } else {
            " "
        }
    } ?: emptyList()
    return labels
}