package com.trio.stride.data.remote.dto

import com.trio.stride.domain.model.ProgressActivity
import com.trio.stride.domain.model.ProgressData
import com.trio.stride.domain.model.ProgressDetails
import com.trio.stride.domain.model.Sport

data class ProgressListDto(
    val data: List<ProgressData>
)

data class ProgressDetailDto(
    val progresses: ProgressDetails,
    val availableSports: List<Sport>
)

data class ProgressActivityDto(
    val distance: Double,
    val elevation: Long,
    val time: Long,
    val activities: List<ProgressActivity>,
)