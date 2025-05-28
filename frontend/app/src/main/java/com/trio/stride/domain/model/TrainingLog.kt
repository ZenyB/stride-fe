package com.trio.stride.domain.model

data class TrainingLogItem(
    val date: Long,
    val color: String,
    val activities: List<TrainingLogActivity>,
    val distance: Long,
    val elevation: Int,
    val time: Long,
)

data class TrainingLogActivity(
    val id: String,
    val name: String,
    val date: Long,
    val sport: TrainingLogSport,
    val distance: Long,
    val elevation: Int,
    val time: Long
)

data class TrainingLogSport(
    val id: String,
    val name: String,
    val image: String,
    val sportMapType: SportMapType?
)

data class TrainingLogFilter(
    val dataType: TrainingLogFilterDataType = TrainingLogFilterDataType.DISTANCE,
    val selectedSports: List<Sport> = emptyList(),
)

enum class TrainingLogFilterDataType { TIME, DISTANCE, ELEVATION }