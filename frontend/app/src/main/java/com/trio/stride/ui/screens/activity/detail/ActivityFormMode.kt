package com.trio.stride.ui.screens.activity.detail

import com.trio.stride.data.remote.dto.CreateActivityRequestDTO
import com.trio.stride.data.remote.dto.UpdateActivityRequestDto
import com.trio.stride.domain.model.Activity
import com.trio.stride.domain.model.Sport

sealed class ActivityFormMode {
    data class Create(
        val sportFromRecord: Sport?,
        val onCreate: (CreateActivityRequestDTO) -> Unit,
        val onDiscard: () -> Unit
    ) : ActivityFormMode()

    data class Update(
        val activity: Activity,
        val onUpdate: (UpdateActivityRequestDto) -> Unit,
        val onDiscard: () -> Unit,
    ) : ActivityFormMode()
}
