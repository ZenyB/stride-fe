package com.trio.stride.ui.components.activity.detail

import android.graphics.Typeface
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.trio.stride.domain.model.ActivityDetailInfo
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.formatSpeed
import com.trio.stride.ui.utils.formatTimeByMillis
import java.text.DecimalFormat

private val YDecimalFormat = DecimalFormat("0.00")
private val startAxisValueFormatter = CartesianValueFormatter.decimal(YDecimalFormat)
private val MarkerValueFormatter = DefaultCartesianMarker.ValueFormatter.default(YDecimalFormat)

val speedMarkerFormatter = DefaultCartesianMarker.ValueFormatter { context, targets ->
    val spannable = SpannableStringBuilder()

    for ((index, target) in targets.withIndex()) {
        val x = target.x
        val yFormatted = MarkerValueFormatter.format(context, listOf(target))

        val yStyled = SpannableString("$yFormatted km/h")
        yStyled.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            yStyled.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        yStyled.setSpan(
            RelativeSizeSpan(1.2f),
            0,
            yStyled.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable.append(yStyled)
        spannable.append("\n")

        val xFormatted = DecimalFormat("#.##").format(x)
        spannable.append("$xFormatted km")

        if (index < targets.lastIndex) {
            spannable.append("\n\n")
        }
    }

    return@ValueFormatter spannable
}

@Composable
fun SpeedChart(item: ActivityDetailInfo, modifier: Modifier = Modifier) {
    val xStep: Double =
        calculateNiceStep(0f, item.distances[item.distances.lastIndex].toFloat()).toDouble()
    val yStep: Double = calculateNiceStep(0f, item.maxSpeed.toFloat(), 6).toDouble()

    Column(modifier) {
        Text(
            "Speed",
            style = StrideTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        CartesianChartWithMarker(
            modifier = Modifier.height(280.dp),
            xItems = item.distances,
            items = item.speeds,
            speedMarkerFormatter,
            color = StrideTheme.colorScheme.primary,
            avgValue = item.avgSpeed,
            startAxisFormatter = startAxisValueFormatter,
            startAxisTitle = "km/h",
            xStep = xStep,
            yStep = yStep
        )

        StatRow("Avg Speed", "${formatSpeed(item.avgSpeed)} km/h")
        StatRow("Max Speed", "${formatSpeed(item.maxSpeed)} km/h")
        StatRow("Moving Time", formatTimeByMillis(item.movingTimeSeconds * 1000))
        StatRow(
            "Elapsed Timed", formatTimeByMillis(item.elapsedTimeSeconds * 1000),
        )
    }
}
