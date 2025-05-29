package com.trio.stride.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.trio.stride.ui.theme.StrideTheme

@Composable
fun Loading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .zIndex(100000f)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                
            },
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(64.dp),
            color = StrideTheme.colorScheme.primary,
            strokeWidth = 12.dp,
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
fun LoadingSmall() {
    CircularProgressIndicator(
        modifier = Modifier.size(24.dp),
        color = StrideTheme.colorScheme.primary,
        strokeCap = StrokeCap.Round,
        strokeWidth = 3.dp
    )
}


@Composable
fun LoadingLarger() {
    CircularProgressIndicator(
        modifier = Modifier.size(44.dp),
        color = StrideTheme.colorScheme.primary,
        strokeCap = StrokeCap.Round,
        strokeWidth = 3.dp
    )
}