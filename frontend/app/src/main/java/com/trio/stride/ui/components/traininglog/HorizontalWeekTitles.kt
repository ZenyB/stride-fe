package com.trio.stride.ui.components.traininglog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.trio.stride.ui.theme.StrideTheme

private val dayOfWeek = listOf("M", "T", "W", "T", "F", "S", "S")

@Composable
fun HorizontalWeekTitles(
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
        dayOfWeek.map {
            Box(
                modifier = Modifier
                    .weight(1f), Alignment.Center
            ) {
                Text(
                    it,
                    style = StrideTheme.typography.labelMedium,
                    color = StrideTheme.colorScheme.onSurface
                )
            }
        }
    }
}