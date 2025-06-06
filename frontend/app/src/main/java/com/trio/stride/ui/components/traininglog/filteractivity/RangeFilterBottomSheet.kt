package com.trio.stride.ui.components.traininglog.filteractivity

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.trio.stride.domain.model.Range
import com.trio.stride.ui.theme.StrideTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RangeFilterBottomSheet(
    title: String,
    dismiss: () -> Unit,
    range: Range,
    value: Range,
    onRangeChanged: (Range) -> Unit,
    modifier: Modifier = Modifier,
    rangeType: RangeType = RangeType.NUMBER,
    visible: Boolean = false
) {
    val unit = when (rangeType) {
        RangeType.NUMBER -> ""
        RangeType.HOUR -> "h"
    }
    val rightRangeText = if (value.max == range.max) ">${range.max}$unit" else "${value.max}$unit"

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(durationMillis = 300)
        ),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(durationMillis = 300)
        ),
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        ModalBottomSheet(
            containerColor = StrideTheme.colorScheme.surface,
            onDismissRequest = { dismiss() },
            modifier = modifier
                .zIndex(10000f)
        ) {
            Row(
                Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    title,
                    style = StrideTheme.typography.titleLarge,
                    color = StrideTheme.colorScheme.onSurface
                )
                IconButton(
                    onClick = { dismiss() }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        modifier = Modifier.size(24.dp),
                        tint = StrideTheme.colorScheme.onSurface
                    )
                }
            }
            HorizontalDivider()
            Box(Modifier.padding(16.dp), Alignment.Center) {
                Column(Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${value.min}$unit", style = StrideTheme.typography.bodyLarge)
                        Text(rightRangeText, style = StrideTheme.typography.bodyLarge)
                    }
                    Spacer(Modifier.height(12.dp))
                    StrideRangeSlider(
                        values = value.min.toFloat()..value.max.toFloat(),
                        range = range,
                        onValueChange = {
                            onRangeChanged(
                                Range(
                                    it.start.toInt(),
                                    it.endInclusive.toInt()
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

enum class RangeType { NUMBER, HOUR }