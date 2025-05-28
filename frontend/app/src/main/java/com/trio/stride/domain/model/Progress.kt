package com.trio.stride.domain.model

import com.google.gson.annotations.SerializedName

enum class ProgressType { DISTANCE, ELEVATION, TIME, ACTIVITY }

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
    val numberActivities: Int,
)

data class ProgressData(
    val sport: Sport,
    val progresses: List<Progress>,
)

data class ProgressActivity(
    val id: String,
    val name: String,
    val mapImage: String,
    val distance: Double,
    val elevation: Long,
    val time: Long,
)

data class ProgressDetails(
    @SerializedName("7D")
    val n7D: List<Progress>?,
    @SerializedName("1M")
    val n1M: List<Progress>?,
    @SerializedName("6M")
    val n6M: List<Progress>?,
    @SerializedName("1Y")
    val n1Y: List<Progress>?,
    @SerializedName("3M")
    val n3M: List<Progress>?,
    @SerializedName("YTD")
    val ytd: List<Progress>?,
)


fun ProgressDetails.isEmpty(): Boolean {
    return n7D.isNullOrEmpty() &&
            n1M.isNullOrEmpty() &&
            n3M.isNullOrEmpty() &&
            n6M.isNullOrEmpty() &&
            n1Y.isNullOrEmpty() &&
            ytd.isNullOrEmpty()
}
