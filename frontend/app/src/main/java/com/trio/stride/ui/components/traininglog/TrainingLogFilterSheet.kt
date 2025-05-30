package com.trio.stride.ui.components.traininglog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.trio.stride.R
import com.trio.stride.domain.model.Sport
import com.trio.stride.domain.model.TrainingLogFilterDataType
import com.trio.stride.ui.theme.StrideTheme

@Composable
fun TrainingLogFilterSheet(
    onDismiss: () -> Unit,
    sports: List<Sport>,
    selectedSports: List<Sport>,
    dataType: TrainingLogFilterDataType,
    onSelectedSportsChange: (List<Sport>) -> Unit,
    onDataTypeChange: (TrainingLogFilterDataType) -> Unit,
    modifier: Modifier = Modifier,
    visible: Boolean = false,
) {
    val showSportTypesSheet = remember { mutableStateOf(false) }
    val showDataTypesSheet = remember { mutableStateOf(false) }

    val dataTypeName = when (dataType) {
        TrainingLogFilterDataType.TIME -> "Time"
        TrainingLogFilterDataType.DISTANCE -> "Distance"
        TrainingLogFilterDataType.ELEVATION -> "Elevation"
    }

    val maxDisplay = 3
    val names = selectedSports.map { it.name }
    val countRemain = names.size - maxDisplay

    val sportsName = when {
        names.isEmpty() -> "No sport"
        names.size <= maxDisplay -> names.joinToString(", ")
        else -> names.take(maxDisplay).joinToString(", ") + " +$countRemain more"
    }

    if (visible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(StrideTheme.colorScheme.scrim.copy(alpha = 0.5f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onDismiss()
                },
            Alignment.BottomCenter
        ) {
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(durationMillis = 500)
                ),
                exit = slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(durationMillis = 500)
                )
            ) {
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .background(
                            StrideTheme.colorScheme.surface,
                            RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                        )
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                awaitFirstDown().also { it.consume() }
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Filters",
                        style = StrideTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider()
                    Column(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Sport Types", style = StrideTheme.typography.labelLarge)
                            Spacer(Modifier.width(16.dp))
                            Text(
                                modifier = Modifier.weight(1f),
                                text = sportsName,
                                textAlign = TextAlign.End,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = StrideTheme.typography.labelLarge,
                                color = StrideTheme.colors.gray
                            )
                            IconButton(
                                onClick = { showSportTypesSheet.value = true }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.park_down_icon),
                                    tint = StrideTheme.colors.gray,
                                    contentDescription = "Show sport types filter",
                                    modifier = Modifier.rotate(270f)
                                )
                            }
                        }
                        HorizontalDivider()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Data", style = StrideTheme.typography.labelLarge)
                            Spacer(Modifier.width(16.dp))
                            Text(
                                modifier = Modifier.weight(1f),
                                text = dataTypeName,
                                textAlign = TextAlign.End,
                                style = StrideTheme.typography.labelLarge,
                                color = StrideTheme.colors.gray
                            )
                            IconButton(
                                onClick = { showDataTypesSheet.value = true }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.park_down_icon),
                                    tint = StrideTheme.colors.gray,
                                    contentDescription = "Show data displayed filter",
                                    modifier = Modifier.rotate(270f)
                                )
                            }
                        }
                    }
                }
            }
            TrainingLogSportTypesSheet(
                onDismiss = { showSportTypesSheet.value = false },
                visible = showSportTypesSheet.value,
                sports = sports,
                initialSelectedSports = selectedSports,
                onChangeSelectedSports = { onSelectedSportsChange(it) }
            )

            TrainingLogDataTypeSheet(
                visible = showDataTypesSheet.value,
                onDismiss = { showDataTypesSheet.value = false },
                dataType = dataType,
                onDataTypeChanged = { onDataTypeChange(it) }
            )
        }
    }
}
