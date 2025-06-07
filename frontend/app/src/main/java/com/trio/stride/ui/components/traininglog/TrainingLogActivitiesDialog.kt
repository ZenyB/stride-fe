package com.trio.stride.ui.components.traininglog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.trio.stride.domain.model.TrainingLogActivity
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.toStringDate

@Composable
fun TrainingLogActivitiesDialog(
    onDismiss: () -> Unit,
    onActivityClick: (TrainingLogActivity) -> Unit,
    date: Long?,
    activities: List<TrainingLogActivity>?,
    modifier: Modifier = Modifier,
    visible: Boolean = false,
) {
    if (visible && activities != null && date != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(StrideTheme.colorScheme.scrim.copy(alpha = 0.5f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple()
                ) {
                    onDismiss()
                },
            Alignment.Center
        ) {
            AnimatedVisibility(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 24.dp)
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            awaitFirstDown().also { it.consume() }
                        }
                    },
                visible = true,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                Box(
                    modifier = modifier
                        .background(
                            StrideTheme.colorScheme.surface,
                            RoundedCornerShape(24.dp)
                        )
                        .padding(horizontal = 12.dp), Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .padding(horizontal = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column {
                                Text(
                                    "Activities",
                                    style = StrideTheme.typography.titleLarge,
                                    color = StrideTheme.colorScheme.onSurface
                                )
                                Text(
                                    date.toStringDate(),
                                    style = StrideTheme.typography.titleSmall,
                                    color = StrideTheme.colorScheme.onSurface
                                )
                            }
                            IconButton(
                                onClick = onDismiss
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Close",
                                    tint = StrideTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(Modifier.height(12.dp))
                        LazyColumn(
                            modifier = Modifier
                                .heightIn(max = 250.dp)
                                .padding(horizontal = 4.dp)
                        ) {
                            itemsIndexed(activities) { index, activity ->
                                TrainingLogActivityItem(
                                    activity = activity,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = ripple()
                                        ) {
                                            onActivityClick(activity)
                                        }
                                )
                                if (index != activities.lastIndex)
                                    Spacer(Modifier.height(32.dp))
                            }
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}