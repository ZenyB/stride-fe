package com.trio.stride.domain.model

data class TrainingLogs(
    val trainingLogs: List<TrainingLogItem>,
    val metaData: TrainingLogMetaData
)

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
    val color: String = "#CCCCCC",
    val sportMapType: SportMapType
)

data class TrainingLogFilter(
    val dataType: TrainingLogFilterDataType = TrainingLogFilterDataType.DISTANCE,
    val selectedSports: List<Sport> = emptyList(),
)

data class TrainingLogMetaData(
    val from: Long,
    val to: Long,
)

enum class TrainingLogFilterDataType { TIME, DISTANCE, ELEVATION }