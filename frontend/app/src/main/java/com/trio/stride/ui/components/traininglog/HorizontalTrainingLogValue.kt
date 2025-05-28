package com.trio.stride.ui.components.traininglog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.trio.stride.domain.model.TrainingLogItem
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.toStringDateRange

@Composable
fun HorizontalTrainingLogValue(
    startDate: Long,
    endDate: Long,
    trainingLogsData: List<TrainingLogItem?>,
    weekDataText: String,
    onItemClick: (TrainingLogItem) -> Unit,
    modifier: Modifier = Modifier,
    todayIndex: Int? = null,
) {
    val dateRange = Pair(startDate, endDate).toStringDateRange()
    var widthPx by remember { mutableStateOf(0) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                dateRange,
                style = StrideTheme.typography.labelLarge,
                color = StrideTheme.colorScheme.onSurface
            )
            Text(
                weekDataText,
                style = StrideTheme.typography.labelLarge,
                color = StrideTheme.colorScheme.onSurface
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            trainingLogsData.map { trainingLogData ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .onGloballyPositioned { layoutCoordinates ->
                            widthPx = layoutCoordinates.size.width
                        }
                        .clickable(
                            enabled = trainingLogData != null,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple()
                        ) {
                            trainingLogData?.let { onItemClick(it) }
                        }, Alignment.Center
                ) {
                    if (trainingLogData != null) {
                        BoxWithBadge(
                            color = Color(trainingLogData.color.toColorInt()),
                            numOfActivities = trainingLogData.activities.size
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(
                                    StrideTheme.colors.gray,
                                    CircleShape
                                )
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if (todayIndex != null)
                List(7) { it }.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .weight(1f), Alignment.Center
                    ) {
                        if (index == todayIndex) {
                            Icon(
                                modifier = Modifier
                                    .size(24.dp)
                                    .rotate(270f),
                                imageVector = Icons.Default.PlayArrow,
                                tint = StrideTheme.colorScheme.onSurface,
                                contentDescription = "Today"
                            )
                        }
                    }
                }
            else
                Spacer(Modifier.height(12.dp))
        }
    }
}