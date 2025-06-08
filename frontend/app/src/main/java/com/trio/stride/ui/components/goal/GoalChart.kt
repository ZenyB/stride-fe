package com.trio.stride.ui.components.goal

import android.graphics.Typeface
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberTop
import com.patrykandpatrick.vico.compose.cartesian.layer.point
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.component.shapeComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.insets
import com.patrykandpatrick.vico.compose.common.rememberHorizontalLegend
import com.patrykandpatrick.vico.compose.common.vicoTheme
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.LegendItem
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import com.patrykandpatrick.vico.core.common.shape.Shape
import com.trio.stride.domain.model.GoalItem
import com.trio.stride.domain.model.GoalType
import com.trio.stride.domain.model.toMonthLabels
import com.trio.stride.ui.components.activity.detail.rememberMarker
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.formatDuration
import com.trio.stride.ui.utils.formatKmDistance
import kotlinx.coroutines.runBlocking

private val LegendLabelKey = ExtraStore.Key<Set<String>>()
private val MarkerValueFormatter = DefaultCartesianMarker.ValueFormatter.default()

@Composable
private fun GoalPointChart(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier,
    months: List<String>,
    goalType: String
) {
    val lineColors = listOf(Color(0xFF687072), Color(0xFF18C252))
    val legendItemLabelComponent = rememberTextComponent(vicoTheme.textColor)
    val solidGuideline = rememberAxisGuidelineComponent(shape = Shape.Rectangle)
    fun formatWithGoalType(value: Double?): String {
        if (value == null) {
            return "0"
        }
        return when (goalType) {
            GoalType.DISTANCE.name -> "${formatKmDistance(value)} km"
            GoalType.ACTIVITY.name -> "${value.toInt()}"
            GoalType.TIME.name -> formatDuration(value.toLong(), false)

            GoalType.ELEVATION.name -> "$value m"
            else -> ""
        }
    }

    val goalMarkerFormatter = DefaultCartesianMarker.ValueFormatter { context, targets ->
        val spannable = SpannableStringBuilder()

        for ((index, target) in targets.withIndex()) {
            val x = target.x
            val yFormatted = MarkerValueFormatter.format(context, listOf(target))
            val values = yFormatted.split(", ")

            val yStyled =
                SpannableString("Goal: ${formatWithGoalType(values[0].toDoubleOrNull())}")
            yStyled.setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                yStyled.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            yStyled.setSpan(
                RelativeSizeSpan(1.1f),
                0,
                yStyled.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spannable.append(yStyled)
            spannable.append("\n")

            val yStyled2 =
                SpannableString("Current: ${formatWithGoalType(values[1].toDoubleOrNull())}")
            yStyled2.setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                yStyled2.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            yStyled2.setSpan(
                RelativeSizeSpan(1.1f),
                0,
                yStyled2.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannable.append(yStyled2)
            if (index < targets.lastIndex) {
                spannable.append("\n\n")
            }
        }

        return@ValueFormatter spannable
    }

    CartesianChartHost(
        rememberCartesianChart(
            rememberLineCartesianLayer(
                LineCartesianLayer.LineProvider.series(
                    lineColors.map { color ->
                        LineCartesianLayer.rememberLine(
                            fill = LineCartesianLayer.LineFill.single(fill(Color.Transparent)),
                            areaFill = null,
                            pointProvider =
                            LineCartesianLayer.PointProvider.single(
                                LineCartesianLayer.point(
                                    rememberShapeComponent(
                                        fill(color),
                                        CorneredShape.Pill
                                    ),
                                    size = 10.dp
                                )
                            ),
                        )
                    }
                )
            ),
            legend =
            rememberHorizontalLegend(
                items = { extraStore ->
                    extraStore[LegendLabelKey].forEachIndexed { index, label ->
                        add(
                            LegendItem(
                                shapeComponent(fill(lineColors[index]), CorneredShape.Pill),
                                legendItemLabelComponent,
                                label,
                            )
                        )
                    }
                },
                padding = insets(top = 12.dp),
            ),
            startAxis = VerticalAxis.rememberStart(
                guideline = null,
                label = null,
                tick = null
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                guideline = solidGuideline,
                valueFormatter = CartesianValueFormatter { context, value, _ ->
                    val formatted = months[value.toInt()]
                    formatted
                },
            ),
            topAxis = HorizontalAxis.rememberTop(
                label = null,
                guideline = solidGuideline,
                tick = null
            ),
            marker = rememberMarker(
                color = StrideTheme.colors.green600,
                valueFormatter = goalMarkerFormatter
            ),
        ),
        modelProducer,
        modifier
            .height(200.dp)
            .offset(y = (-24).dp),
        rememberVicoScrollState(scrollEnabled = false),
    )
}

@Composable
fun GoalChart(item: GoalItem) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val goalMap = mutableMapOf<Int, Number>()
    val actualMap = mutableMapOf<Int, Number>()

    item.histories?.forEachIndexed { index, history ->
        goalMap[index] = history.amountGoal
        actualMap[index] = history.amountGain
    }

    val months = item.toMonthLabels()

    val chartData: Map<String, Map<Int, Number>> = mapOf(
        "Goal" to goalMap,
        "Actual" to actualMap
    )
    runBlocking {
        modelProducer.runTransaction {
            lineSeries { chartData.forEach { (_, map) -> series(map.keys, map.values) } }
            extras { extraStore -> extraStore[LegendLabelKey] = chartData.keys }
        }
    }
    GoalPointChart(modelProducer, months = months, goalType = item.type)
}


@Composable
@Preview(showBackground = true)
private fun Preview() {
    GoalChart(goalItem)
}
