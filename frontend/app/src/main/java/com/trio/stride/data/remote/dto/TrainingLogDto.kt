package com.trio.stride.data.remote.dto

import com.trio.stride.domain.model.SportMapType
import com.trio.stride.ui.utils.getEndOfWeekInMillis
import com.trio.stride.ui.utils.getStartOf12WeeksInMillis

data class TrainingLogsResponseDto(
    val data: List<TrainingLogItemDto>,
    val filterDto: TrainingLogFilterDto,
)

data class TrainingLogFilterDto(
    val userId: String = "",
    val sportIds: List<String>? = null,
    val fromDate: Long = getStartOf12WeeksInMillis(),
    val toDate: Long = getEndOfWeekInMillis(),
)

data class TrainingLogItemDto(
    val date: Long,
    val color: String,
    val activities: List<TrainingLogActivityDto>,
    val distance: Long,
    val elevation: Int,
    val time: Long,
)

data class TrainingLogActivityDto(
    val id: String,
    val name: String,
    val date: Long,
    val sport: TrainingLogSportDto,
    val distance: Long,
    val elevation: Int,
    val time: Long
)

data class TrainingLogSportDto(
    val id: String,
    val name: String,
    val image: String,
    val sportMapType: SportMapType?
)