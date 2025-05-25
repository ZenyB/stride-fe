package com.trio.stride.data.remote.dto

import com.trio.stride.domain.model.ProgressData
import com.trio.stride.domain.model.ProgressDetail
import com.trio.stride.domain.model.ProgressTimeRange
import com.trio.stride.domain.model.ProgressType

data class ProgressListDto(
    val data: List<ProgressData>
)

data class ProgressDetailDto(
    val data: List<ProgressDetail>
)

data class ProgressDetailRequestDto(
    val sportId: String,
    val type: String? = ProgressTimeRange.LAST_3_MONTHS.label,
    val timeFrame: String? = ProgressType.DISTANCE.name,
)