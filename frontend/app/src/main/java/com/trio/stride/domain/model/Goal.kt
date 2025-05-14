package com.trio.stride.domain.model

import com.trio.stride.ui.utils.formatDuration
import com.trio.stride.ui.utils.formatKmDistance
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
    val key: String,
    val amountGain: Long,
    val amountGoal: Long,
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
