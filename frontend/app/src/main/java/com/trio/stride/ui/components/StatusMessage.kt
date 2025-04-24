package com.trio.stride.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trio.stride.ui.theme.StrideColor
import com.trio.stride.ui.theme.StrideTheme

@Composable
fun StatusMessage(
    text: String,
    type: StatusMessageType,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (type) {
        StatusMessageType.ERROR -> StrideColor.secondary
        StatusMessageType.PROCESSING -> StrideColor.primary600
        StatusMessageType.SUCCESS -> StrideColor.green600
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            style = StrideTheme.typography
                .headlineMedium.copy(fontSize = 14.sp),
            color = Color.White,
        )
    }
}

enum class StatusMessageType {
    ERROR, SUCCESS, PROCESSING
}
