package com.trio.stride.ui.components.traininglog.filteractivity

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.trio.stride.domain.model.DateRange
import com.trio.stride.ui.components.textfield.CalendarTextField
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.toDate
import com.trio.stride.ui.utils.toLocalDate
import com.trio.stride.ui.utils.toMillis
import com.trio.stride.ui.utils.toStringDate
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangeBottomSheet(
    dismiss: () -> Unit,
    onDateRangeChanged: (DateRange) -> Unit,
    modifier: Modifier = Modifier,
    visible: Boolean = false,
    dateRange: DateRange = DateRange(),
) {
    val isDateRangeOn = remember { mutableStateOf(true) }
    val ownDateRange = remember { mutableStateOf(dateRange) }
    
    LaunchedEffect(isDateRangeOn) {
        if (!isDateRangeOn.value) {
            ownDateRange.value = ownDateRange.value.copy(max = null)
        }
    }

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
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                text = "Dates",
                textAlign = TextAlign.Center,
                style = StrideTheme.typography.titleMedium,
                color = StrideTheme.colorScheme.onSurface
            )
            HorizontalDivider()
            Column(
                Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        "Date Range",
                        style = StrideTheme.typography.bodyMedium,
                        color = StrideTheme.colorScheme.onSurface
                    )
                    Checkbox(
                        checked = isDateRangeOn.value,
                        onCheckedChange = { isDateRangeOn.value = it },
                        modifier = Modifier.size(32.dp)
                    )
                }
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        Modifier
                            .weight(1f)
                            .animateContentSize(animationSpec = tween(500))
                    ) {
                        CalendarTextField(
                            initialDate = ownDateRange.value.min?.toLocalDate() ?: LocalDate.now(),
                            value = ownDateRange.value.min?.toStringDate() ?: "dd/mm/yyyy",
                            label = { Text("Start") },
                            onDateChange = {
                                val selectedMin = it.toDate().toMillis()
                                val currentMax = ownDateRange.value.max

                                val (finalMin, finalMax) = if (currentMax != null && selectedMin > currentMax) {
                                    Pair(currentMax, selectedMin)
                                } else {
                                    Pair(selectedMin, currentMax)
                                }

                                ownDateRange.value =
                                    ownDateRange.value.copy(min = finalMin, max = finalMax)
                            }

                        )
                    }
                    if (isDateRangeOn.value) {
                        Box(
                            Modifier
                                .weight(1f)
                                .animateContentSize(animationSpec = tween(500))
                        ) {
                            CalendarTextField(
                                initialDate = ownDateRange.value.max?.toLocalDate()
                                    ?: LocalDate.now(),
                                value = ownDateRange.value.max?.toStringDate() ?: "dd/mm/yyyy",
                                label = { Text("End") },
                                onDateChange = {
                                    val currentMin = ownDateRange.value.min
                                    val selectedMax = it.toDate().toMillis()

                                    val (finalMin, finalMax) = if (currentMin != null && currentMin > selectedMax) {
                                        Pair(selectedMax, currentMin)
                                    } else {
                                        Pair(currentMin, selectedMax)
                                    }

                                    ownDateRange.value =
                                        ownDateRange.value.copy(min = finalMin, max = finalMax)
                                }

                            )
                        }
                    }
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = ownDateRange.value.min != null && ownDateRange.value.max != null,
                    onClick = { onDateRangeChanged(ownDateRange.value) },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save", style = StrideTheme.typography.titleMedium)
                }
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = ownDateRange.value.min != null && ownDateRange.value.max != null,
                    onClick = { ownDateRange.value = DateRange() },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Clear", style = StrideTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    val show = remember { mutableStateOf(true) }
    val dateRange = remember { mutableStateOf(DateRange()) }
    DateRangeBottomSheet(
        visible = show.value,
        dismiss = { show.value = false },
        onDateRangeChanged = { dateRange.value = it },
        dateRange = dateRange.value
    )
}