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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.trio.stride.domain.model.TrainingLogFilterDataType
import com.trio.stride.ui.theme.StrideTheme

@Composable
fun TrainingLogDataTypeSheet(
    onDismiss: () -> Unit,
    dataType: TrainingLogFilterDataType,
    onDataTypeChanged: (TrainingLogFilterDataType) -> Unit,
    modifier: Modifier = Modifier,
    visible: Boolean = false,
) {
    if (visible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.navigationBars)
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
                    animationSpec = tween(durationMillis = 300)
                ),
                exit = slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(durationMillis = 300)
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
                        "Data Displayed",
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
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Time", style = StrideTheme.typography.labelLarge)
                            RadioButton(
                                selected = dataType == TrainingLogFilterDataType.TIME,
                                onClick = { onDataTypeChanged(TrainingLogFilterDataType.TIME) }
                            )
                        }
                        HorizontalDivider()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Distance", style = StrideTheme.typography.labelLarge)
                            RadioButton(
                                selected = dataType == TrainingLogFilterDataType.DISTANCE,
                                onClick = { onDataTypeChanged(TrainingLogFilterDataType.DISTANCE) }
                            )
                        }
                        HorizontalDivider()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Elevation", style = StrideTheme.typography.labelLarge)
                            RadioButton(
                                selected = dataType == TrainingLogFilterDataType.ELEVATION,
                                onClick = { onDataTypeChanged(TrainingLogFilterDataType.ELEVATION) }
                            )
                        }
                    }
                }
            }
        }
    }
}