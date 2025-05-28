package com.trio.stride.ui.components.traininglog

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.toStringDateRange
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HorizontalTrainingLogSkeleton(
    startDates: List<Long>,
    endDates: List<Long>,
    modifier: Modifier = Modifier
) {
    val dateRanges = startDates.mapIndexed { index, startDate ->
        Pair(
            startDate,
            endDates[index]
        ).toStringDateRange()
    }

    dateRanges.mapIndexed() { index, dateRange ->
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                dateRange,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
                style = StrideTheme.typography.labelLarge,
                color = StrideTheme.colorScheme.onSurface
            )
            BouncingDots()
            Spacer(Modifier.height(12.dp))
            if (index != dateRanges.lastIndex) {
                HorizontalDivider()
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun BouncingDots(
    dotCount: Int = 7,
    minSize: Dp = 4.dp,
    maxSize: Dp = 10.dp,
    durationPerDot: Int = 500,
    delayBetweenDots: Int = 100
) {
    val dots = remember { List(dotCount) { Animatable(minSize.value) } }

    LaunchedEffect(Unit) {
        while (true) {
            dots.forEachIndexed { index, anim ->
                launch {
                    anim.animateTo(
                        targetValue = maxSize.value,
                        animationSpec = tween(durationMillis = durationPerDot)
                    )
                }
                delay(delayBetweenDots.toLong())
            }

            delay(100)
            dots.forEachIndexed { index, anim ->
                launch {
                    anim.animateTo(
                        targetValue = minSize.value,
                        animationSpec = tween(durationMillis = durationPerDot)
                    )
                }
                delay(delayBetweenDots.toLong())
            }
            delay(500)
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        dots.forEach { animatable ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f), Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(animatable.value.dp)
                        .border(1.dp, StrideTheme.colors.gray500, CircleShape)
                        .clip(CircleShape)
                )
            }
        }
    }
}