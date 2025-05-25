package com.trio.stride.ui.screens.progress

import android.text.Layout
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberEnd
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberTop
import com.patrykandpatrick.vico.compose.cartesian.layer.point
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.insets
import com.patrykandpatrick.vico.core.cartesian.CartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarkerVisibilityListener
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.LineCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.core.common.LayeredComponent
import com.patrykandpatrick.vico.core.common.component.ShapeComponent
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import com.patrykandpatrick.vico.core.common.shape.Shape
import com.trio.stride.domain.model.Progress
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.formatKmDistance
import kotlinx.coroutines.runBlocking

@Composable
fun ProgressView() {

}


private val LegendLabelKey = ExtraStore.Key<Set<String>>()
private val MarkerValueFormatter = DefaultCartesianMarker.ValueFormatter.default()

@Composable
private fun ProgressPointChart(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier,
) {
    val solidGuideline = rememberAxisGuidelineComponent(shape = Shape.Rectangle)
    var currentTargets = emptyList<CartesianMarker.Target>()

    val xLabel =
        rememberTextComponent(
            color = StrideTheme.colors.gray600,
            textAlignment = Layout.Alignment.ALIGN_CENTER,
            textSize = 12.sp,
            padding = insets(4.dp, 4.dp),
        )

    var selectedXTarget by remember { mutableStateOf<Double?>(null) }
    val marker = rememberProgressMarker(color = StrideTheme.colorScheme.primary)

    val persistentMarker = remember(selectedXTarget) {
        val persistentMarkerScope: CartesianChart.PersistentMarkerScope.(ExtraStore) -> Unit = {
            selectedXTarget?.let { marker at it }
        }
        persistentMarkerScope
    }

    val markerVisibilityListener = remember {
        object : CartesianMarkerVisibilityListener {
            override fun onShown(marker: CartesianMarker, targets: List<CartesianMarker.Target>) {
                val target = targets.firstOrNull() as? LineCartesianLayerMarkerTarget ?: return
                selectedXTarget = (if (target.x == selectedXTarget) null else target.x)
            }

            override fun onUpdated(marker: CartesianMarker, targets: List<CartesianMarker.Target>) {
                val target = targets.firstOrNull() as? LineCartesianLayerMarkerTarget ?: return
                selectedXTarget = (if (target.x == selectedXTarget) null else target.x)
            }
            
        }
    }

    CartesianChartHost(
        rememberCartesianChart(
            rememberLineCartesianLayer(
                LineCartesianLayer.LineProvider.series(
                    LineCartesianLayer.rememberLine(
                        fill = LineCartesianLayer.LineFill.single(fill(StrideTheme.colorScheme.primary)),
                        areaFill = null,
                        pointProvider =
                        LineCartesianLayer.PointProvider.single(
                            LineCartesianLayer.point(
                                rememberShapeComponent(
                                    fill(StrideTheme.colorScheme.surface),
                                    strokeFill = fill(StrideTheme.colorScheme.primary),
                                    strokeThickness = 2.dp,
                                    shape = CorneredShape.Pill
                                ),
                                size = 10.dp
                            )
                        ),
                    )
                )
            ),
            startAxis = VerticalAxis.rememberStart(
                guideline = null,
                label = null,
                tick = null
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                guideline = null,
//                valueFormatter = CartesianValueFormatter { context, value, _ ->
//                    val formatted = months[value.toInt()]
//                    formatted
//                }
            ),
            topAxis = HorizontalAxis.rememberTop(
                label = null,
                guideline = null,
                tick = null
            ),
            endAxis = VerticalAxis.rememberEnd(
                guideline = solidGuideline,
                tick = null,
                label = xLabel,
                valueFormatter = CartesianValueFormatter { context, value, _ ->
                    val formatted = "${formatKmDistance(value)} km"
                    formatted
                }
            ),
            marker = marker,
            markerVisibilityListener = markerVisibilityListener,
            persistentMarkers = persistentMarker
        ),
        modelProducer,
        modifier
            .height(200.dp),
        rememberVicoScrollState(scrollEnabled = false),
    )
}

@Composable
fun ProgressChart(items: List<Progress>) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val progressMap = mutableMapOf<Int, Number>()

    items.forEachIndexed { index, item ->
        progressMap[index] = item.distance
    }

    runBlocking {
        modelProducer.runTransaction {
            lineSeries { series(progressMap.keys, progressMap.values) }
        }
    }
    ProgressPointChart(modelProducer)
}


val progressSample: List<Progress> = listOf(
    Progress(
        fromDate = 1739552400000,
        toDate = 1740157199999,
        distance = 0.0,
        elevation = 0,
        time = 0,
        numberActivities = 0
    ),
    Progress(
        fromDate = 1740157200000,
        toDate = 1740761999999,
        distance = 0.0,
        elevation = 0,
        time = 0,
        numberActivities = 0
    ),
    Progress(
        fromDate = 1740762000000,
        toDate = 1741366799999,
        distance = 8.2,
        elevation = 0,
        time = 0,
        numberActivities = 0
    ),
    Progress(
        fromDate = 1741366800000,
        toDate = 1741971599999,
        distance = 0.0,
        elevation = 0,
        time = 0,
        numberActivities = 0
    ), Progress(
        fromDate = 1741971600000,
        toDate = 1742576399999,
        distance = 2.3,
        elevation = 0,
        time = 0,
        numberActivities = 0
    )
)

@Composable
@Preview(showBackground = true)
private fun Preview() {
    ProgressChart(progressSample)
}

@Composable
internal fun rememberProgressMarker(
    valueFormatter: DefaultCartesianMarker.ValueFormatter =
        DefaultCartesianMarker.ValueFormatter.default(),
    color: Color
): CartesianMarker {
    val label = rememberTextComponent(
        color = Color.Transparent,
        textSize = 0.sp
    )

    val indicatorFrontComponent =
        rememberShapeComponent(fill(StrideTheme.colorScheme.primary), CorneredShape.Pill)
    val guideline = rememberAxisGuidelineComponent(
        shape = Shape.Rectangle, fill = fill(Color.Black),
        thickness = 2.dp
    )
    return rememberDefaultCartesianMarker(
        label = label,
        indicator =
        { _ ->
            LayeredComponent(
                back = ShapeComponent(fill(color.copy(alpha = 0.15f)), CorneredShape.Pill),
                front =
                LayeredComponent(
                    back = ShapeComponent(
                        fill = fill(color),
                        shape = CorneredShape.Pill
                    ),
                    front = indicatorFrontComponent,
                    padding = insets(3.dp),
                ),
                padding = insets(6.dp),
            )
        },
        indicatorSize = 20.dp,
        guideline = guideline,
    )
}