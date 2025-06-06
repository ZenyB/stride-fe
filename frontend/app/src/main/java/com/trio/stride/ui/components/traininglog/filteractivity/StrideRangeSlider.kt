package com.trio.stride.ui.components.traininglog.filteractivity

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.trio.stride.domain.model.Range
import com.trio.stride.ui.theme.StrideTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StrideRangeSlider(
    range: Range = Range(0, 10),
    values: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit
) {
    val thumbRadius = 12.dp
    val trackHeight = 4.dp

    RangeSlider(
        value = values,
        onValueChange = { newRange ->
            onValueChange(newRange.start..newRange.endInclusive)
        },
        valueRange = range.min.toFloat()..range.max.toFloat(),
        steps = 0,
        modifier = Modifier
            .fillMaxWidth()
            .height(thumbRadius * 2),
        colors = SliderDefaults.colors(
            activeTrackColor = StrideTheme.colorScheme.primary,
            inactiveTrackColor = StrideTheme.colorScheme.background,
            thumbColor = StrideTheme.colorScheme.primary
        ),
        startThumb = {
            Box(
                modifier = Modifier
                    .size(thumbRadius * 2)
                    .background(StrideTheme.colors.gray600, shape = CircleShape)
                    .border(width = 5.dp, StrideTheme.colorScheme.surface, CircleShape)
            )
        },
        endThumb = {
            Box(
                modifier = Modifier
                    .size(thumbRadius * 2)
                    .background(StrideTheme.colors.gray600, shape = CircleShape)
                    .border(width = 5.dp, StrideTheme.colorScheme.surface, CircleShape)
            )
        },
        track = {
            val startFraction = (range.min - values.start) / (values.endInclusive - values.start)
            val endFraction = (range.max - values.start) / (values.endInclusive - values.start)

            Box(
                Modifier
                    .fillMaxWidth()
                    .height(trackHeight)
                    .background(StrideTheme.colors.grayBorder, shape = RoundedCornerShape(50))
            ) {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = endFraction - startFraction)
                        .align(Alignment.CenterStart)
                        .padding(start = with(LocalDensity.current) { (startFraction * (LocalConfiguration.current.screenWidthDp.dp).toPx()).toDp() })
                        .background(StrideTheme.colorScheme.primary, shape = RoundedCornerShape(50))
                )
            }
        }
    )
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    val range = remember { mutableStateOf(2.toFloat()..8f.toFloat()) }

    StrideRangeSlider(
        values = range.value,
        range = Range(0, 10),
        onValueChange = { range.value = it.start..it.endInclusive }
    )
}
