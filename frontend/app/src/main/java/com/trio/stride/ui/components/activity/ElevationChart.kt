package com.trio.stride.ui.components.activity

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.trio.stride.domain.model.ActivityDetailInfo
import com.trio.stride.ui.theme.StrideTheme
import java.text.DecimalFormat

private val YDecimalFormat = DecimalFormat("0")
private val MarkerValueFormatter = DefaultCartesianMarker.ValueFormatter.default(YDecimalFormat)
private val startAxisValueFormatter = CartesianValueFormatter.decimal(YDecimalFormat)

val elevationMarkerFormatter = DefaultCartesianMarker.ValueFormatter { context, targets ->
    val spannable = SpannableStringBuilder()

    for ((index, target) in targets.withIndex()) {
        val x = target.x
        val yFormatted = MarkerValueFormatter.format(context, listOf(target))

        val yStyled = SpannableString("$yFormatted m")
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
fun ElevationChart(item: ActivityDetailInfo, modifier: Modifier = Modifier) {
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            lineSeries { series(item.elevations) }
        }
    }
    Column(modifier) {
        Text(
            "Elevation",
            style = StrideTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        CartesianChartWithMarker(
            modifier = Modifier.height(280.dp),
            modelProducer,
            elevationMarkerFormatter,
            color = StrideTheme.colors.gray500,
            startAxisTitle = "m",
            startAxisFormatter = startAxisValueFormatter
        )

        StatRow("Elevation Gain", "${item.elevationGain} m")
        StatRow("Max Elevation", "${item.maxElevation} m")
    }
}
