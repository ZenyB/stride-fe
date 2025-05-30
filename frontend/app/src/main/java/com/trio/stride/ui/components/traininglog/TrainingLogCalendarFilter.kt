package com.trio.stride.ui.components.traininglog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.systemZoneId
import java.time.Instant
import java.time.YearMonth

@Composable
fun TrainingLogCalendarFilter(
    visible: Boolean,
    startDate: Long?,
    onMonthSelect: (YearMonth) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (startDate != null) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(500)),
            exit = fadeOut(tween(500))
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(StrideTheme.colorScheme.scrim.copy(alpha = 0.5f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple()
                        ) {
                            onDismiss()
                        })
                AnimatedVisibility(
                    visible = true,
                    modifier = modifier
                        .fillMaxWidth(2f / 3f)
                        .fillMaxHeight()
                        .align(Alignment.CenterEnd)
                        .background(StrideTheme.colorScheme.surface)
                        .windowInsetsPadding(WindowInsets.statusBars.add(WindowInsets.navigationBars))
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                awaitFirstDown().also { it.consume() }
                            }
                        },
                    enter = slideInHorizontally(
                        animationSpec = tween(500),
                        initialOffsetX = { it }
                    ),
                    exit = slideOutHorizontally(
                        animationSpec = tween(500),
                        targetOffsetX = { it }
                    )
                ) {
                    Column(
                        modifier = modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(12.dp)
                    ) {
                        var currentMonth = YearMonth.now()
                        val startYearMonth = Instant.ofEpochMilli(startDate)
                            .atZone(systemZoneId)
                            .toLocalDate()
                            .let { YearMonth.from(it) }
                        var year = currentMonth.year
                        var janOfCurrentYear = YearMonth.of(currentMonth.year, 1).month
                        Text(
                            year.toString(),
                            style = StrideTheme.typography.titleLarge,
                            color = StrideTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(8.dp))
                        while (currentMonth >= startYearMonth) {
                            val month = currentMonth
                            if (currentMonth.month == janOfCurrentYear) {
                                year -= 1
                                janOfCurrentYear = janOfCurrentYear.minus(12)
                                Text(
                                    year.toString(),
                                    style = StrideTheme.typography.titleLarge,
                                    color = StrideTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.height(8.dp))
                            }
                            Text(
                                currentMonth.month.name,
                                style = StrideTheme.typography.labelLarge,
                                color = StrideTheme.colors.gray,
                                modifier = Modifier.clickable {
                                    onMonthSelect(month)
                                }
                            )
                            if (currentMonth > startYearMonth) {
                                Spacer(Modifier.height(8.dp))
                                HorizontalDivider()
                                Spacer(Modifier.height(8.dp))
                            }
                            currentMonth = currentMonth.minusMonths(1)
                        }
                    }
                }
            }
        }
    }
}