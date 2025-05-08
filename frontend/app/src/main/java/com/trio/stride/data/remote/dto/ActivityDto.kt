package com.trio.stride.data.remote.dto

import com.trio.stride.domain.model.ActivityItem

data class ActivityListDto(
    val data: List<ActivityItem>,
    val page: PageDto
)