package com.trio.stride.domain.model


enum class ProgressType { TIME, DISTANCE, ELEVATION, ACTIVITY }

enum class ProgressTimeRange(val label: String) {
    LAST_7_DAYS("7D"),
    LAST_1_MONTH("1M"),
    LAST_3_MONTHS("3M"),
    LAST_6_MONTHS("6M"),
    YEAR_TO_DATE("YTD"),
    LAST_1_YEAR("1Y");

    override fun toString(): String = label
}

data class Progress(
    val fromDate: Long,
    val toDate: Long,
    val distance: Double,
    val elevation: Long,
    val time: Long,
    val numberActivities: Long,
)

data class ProgressData(
    val sport: Sport,
    val progress: List<Progress>,
)

data class ProgressDetail(
    val amount: Long,
    val fromDate: Long,
    val toDate: Long,
)