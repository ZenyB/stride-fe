package com.trio.stride.ui.components.progress

import android.text.Layout
import android.util.Log
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberEnd
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberTop
import com.patrykandpatrick.vico.compose.cartesian.cartesianLayerPadding
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
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
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
import com.trio.stride.domain.model.ProgressType
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.formatDistance
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sign

val yStepExtraKey = ExtraStore.Key<Double>()
val xLabelExtraKey = ExtraStore.Key<List<String>>()
val xStepExtraKey = ExtraStore.Key<Double>()


@Composable
private fun ProgressPointChart(
    modifier: Modifier = Modifier,
    filterType: ProgressType? = null,
    modelProducer: CartesianChartModelProducer,
    hasPoint: Boolean? = true,
    labels: List<String>,
    yFormatter: CartesianValueFormatter? = null,
    hasMarker: Boolean? = false,
    onIndexSelected: (
        index: Int?
    ) -> Unit
) {
    val solidGuideline = rememberAxisGuidelineComponent(shape = Shape.Rectangle)

    val xLabel =
        rememberTextComponent(
            color = StrideTheme.colors.gray600,
            textAlignment = Layout.Alignment.ALIGN_CENTER,
            textSize = 10.sp,
            padding = insets(4.dp, 4.dp),
        )
    val defaultSelect = (labels.size - 1).toDouble()
    var selectedXTarget by remember { mutableStateOf<Double?>(defaultSelect) }

    val marker =
        rememberProgressMarker(color = StrideTheme.colorScheme.primary)

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

    LaunchedEffect(selectedXTarget) {
        onIndexSelected(selectedXTarget?.toInt())
    }
    if (hasMarker == true) {
        LaunchedEffect(filterType) {
            selectedXTarget = null
        }
    }

    CartesianChartHost(
        rememberCartesianChart(
            rememberLineCartesianLayer(
                LineCartesianLayer.LineProvider.series(
                    LineCartesianLayer.rememberLine(
                        fill = LineCartesianLayer.LineFill.single(fill(StrideTheme.colorScheme.primary)),
                        areaFill =
                        LineCartesianLayer.AreaFill.single(
                            fill(
                                StrideTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                        ),
                        pointProvider = if (hasPoint == true)
                            LineCartesianLayer.PointProvider.single(
                                LineCartesianLayer.point(
                                    rememberShapeComponent(
                                        fill(StrideTheme.colorScheme.surface),
                                        strokeFill = fill(StrideTheme.colorScheme.primary),
                                        strokeThickness = 2.dp,
                                        shape = CorneredShape.Pill
                                    ),
                                    size = 8.dp
                                )
                            ) else null,
                    )
                ),
                rangeProvider = AutoStep
            ),
            startAxis = VerticalAxis.rememberStart(
                guideline = null,
                label = null,
                tick = null,
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                guideline = null,
                valueFormatter = { context, value, _ ->
                    if (value.toInt() < labels.size && value.toInt() > -1) {
                        Log.d("get weeks label chart", "label index: ${value.toInt()}")
                        Log.d("get weeks label chart", "label value: ${labels[value.toInt()]}")
                        val formatted = labels[value.toInt()]
                        formatted
                    } else
                        zeroWidthSpace
                },
                itemPlacer = HorizontalAxis.ItemPlacer.segmented()

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
                valueFormatter = yFormatter ?: CartesianValueFormatter { context, value, _ ->
                    val formatted = "${formatDistance(value)} km"
                    formatted
                },
                itemPlacer = remember {
                    VerticalAxis.ItemPlacer.step({ extraStore ->
                        extraStore.getOrNull(yStepExtraKey)
                    })
                },
            ),
            marker = marker,
            markerVisibilityListener = markerVisibilityListener,
            persistentMarkers = persistentMarker,
            getXStep = { model ->
                model.extraStore.getOrNull(xStepExtraKey) ?: model.getXDeltaGcd()
            },
            layerPadding = { extraStore ->
                cartesianLayerPadding(
                    unscalableStart = 4.dp,
                    unscalableEnd = 4.dp,
                    scalableStart = 10.dp,
                    scalableEnd = 10.dp
                )
            }
        ),
        modelProducer,
        modifier
            .height(if (hasMarker == true) 250.dp else 200.dp),
        rememberVicoScrollState(scrollEnabled = false),
    )
}

@Composable
fun ProgressChart(
    items: List<Progress>,
    yStep: Double,
    xStep: Double? = 4.0,
    hasMarker: Boolean? = false,
    yFormatter: CartesianValueFormatter? = null,
    labels: List<String>,
    filterType: ProgressType? = ProgressType.DISTANCE,
    onIndexSelected: (index: Int?) -> Unit
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val progressMap = remember(filterType, items) {
        buildMap<Int, Number> {
            items.forEachIndexed { index, item ->
                when (filterType) {
                    ProgressType.DISTANCE -> put(index, item.distance)
                    ProgressType.ELEVATION -> put(index, item.elevation)
                    ProgressType.TIME -> put(index, item.time)
                    ProgressType.ACTIVITY -> put(index, item.numberActivities)
                    null -> {}
                }
            }
        }
    }
    LaunchedEffect(progressMap, filterType) {
        modelProducer.runTransaction {
            lineSeries { series(progressMap.keys, progressMap.values) }
            extras { extraStore ->
                extraStore[yStepExtraKey] = yStep
                extraStore[xLabelExtraKey] = labels
                xStep?.let { extraStore[xStepExtraKey] = it }
            }
        }
    }
    if (hasMarker == true) {
        ProgressPointChart(
            filterType = filterType,
            hasMarker = hasMarker,
            modelProducer = modelProducer,
            labels = labels,
            yFormatter = yFormatter,
            onIndexSelected = onIndexSelected,
            hasPoint = labels.size <= 31
        )
    } else {
        ProgressPointChart(
            modelProducer = modelProducer,
            labels = labels,
            yFormatter = yFormatter,
            onIndexSelected = onIndexSelected
        )
    }
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
        indicatorSize = 18.dp,
        guideline = guideline,
    )
}

private object AutoStep : CartesianLayerRangeProvider {
    override fun getMinY(minY: Double, maxY: Double, extraStore: ExtraStore) =
        if (minY == 0.0 && maxY == 0.0 || minY >= 0.0) 0.0 else minY.round(maxY)

    override fun getMaxY(minY: Double, maxY: Double, extraStore: ExtraStore) =
        when {
            minY == 0.0 && maxY == 0.0 -> 1.0
            maxY <= 0.0 -> 0.0
            else -> {
                val step = extraStore.getOrNull(yStepExtraKey) ?: 1.0
                ceil(maxY.round(minY) / step) * step
            }
        }

    private fun Double.round(other: Double): Double {
        val absoluteValue = abs(this)
        val base = 10.0.pow(floor(log10(max(absoluteValue, abs(other)))) - 1)
        return sign * ceil(absoluteValue / base) * base
    }
}