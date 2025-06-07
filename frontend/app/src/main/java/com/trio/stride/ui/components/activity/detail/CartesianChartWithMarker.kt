package com.trio.stride.ui.components.activity.detail

import android.text.Layout
import androidx.compose.foundation.layout.offset
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
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.insets
import com.patrykandpatrick.vico.compose.common.shape.dashedShape
import com.patrykandpatrick.vico.compose.common.shape.markerCorneredShape
import com.patrykandpatrick.vico.compose.common.shape.rounded
import com.patrykandpatrick.vico.core.cartesian.CartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.decoration.HorizontalLine
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarkerVisibilityListener
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.LayeredComponent
import com.patrykandpatrick.vico.core.common.component.ShapeComponent
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import com.patrykandpatrick.vico.core.common.shape.Shape
import com.trio.stride.ui.theme.StrideTheme
import java.text.DecimalFormat
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow


@Composable
fun CartesianChartWithMarker(
    modifier: Modifier = Modifier,
    xItems: Collection<Number>,
    items: Collection<Number>,
    markerFormatter: DefaultCartesianMarker.ValueFormatter,
    startAxisFormatter: CartesianValueFormatter,
    startAxisTitle: String,
    color: Color,
    avgValue: Double? = null,
    xStep: Double? = null,
    yStep: Double? = null
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    val xStepExtraKey = ExtraStore.Key<Double>()
    val yStepExtraKey = ExtraStore.Key<Double>()

    val marker = rememberMarker(markerFormatter, color)

    val solidGuideline = rememberAxisGuidelineComponent(shape = Shape.Rectangle)
    var selectedXTarget by remember { mutableStateOf<Double?>(null) }
    var selectedActualXTarget by remember { mutableStateOf<Double?>(null) }


    val persistentMarker = remember(selectedActualXTarget) {
        val persistentMarkerScope: CartesianChart.PersistentMarkerScope.(ExtraStore) -> Unit = {
            selectedActualXTarget?.let { marker at it }
        }

        persistentMarkerScope
    }

    val markerVisibilityListener = remember {
        object : CartesianMarkerVisibilityListener {
            override fun onShown(marker: CartesianMarker, targets: List<CartesianMarker.Target>) {
                super.onShown(marker, targets)
                val target = targets.firstOrNull() ?: return
                selectedXTarget = (if (target.x == selectedXTarget) null else target.x)
                selectedActualXTarget = null

            }

            override fun onUpdated(marker: CartesianMarker, targets: List<CartesianMarker.Target>) {
                super.onUpdated(marker, targets)
                selectedActualXTarget = null
                val target = targets.firstOrNull() ?: return
                selectedXTarget = (if (target.x == selectedXTarget) null else target.x)
            }

            override fun onHidden(marker: CartesianMarker) {
                super.onHidden(marker)
                selectedActualXTarget = selectedXTarget
            }
        }
    }

    val label =
        rememberTextComponent(
            color = StrideTheme.colors.gray600,
            textAlignment = Layout.Alignment.ALIGN_CENTER,
            padding = insets(10.dp, 4.dp),
            minWidth = TextComponent.MinWidth.fixed(20F),
            textSize = 12.sp,
        )
    val xLabel =
        rememberTextComponent(
            color = StrideTheme.colors.gray600,
            textAlignment = Layout.Alignment.ALIGN_CENTER,
            padding = insets(0.dp, 4.dp),
            minWidth = TextComponent.MinWidth.fixed(20F),
            textSize = 12.sp,
        )

    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            lineSeries { series(xItems, items) }
            extras { extraStore ->
                xStep?.let { extraStore[xStepExtraKey] = it }
                yStep?.let { extraStore[yStepExtraKey] = it }
            }
        }
    }

    CartesianChartHost(
        rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider =
                LineCartesianLayer.LineProvider.series(
                    LineCartesianLayer.rememberLine(
                        fill = LineCartesianLayer.LineFill.single(fill(color.copy(0.7f))),
                        areaFill =
                        LineCartesianLayer.AreaFill.single(
                            fill(
                                color.copy(alpha = 0.7f)
                            )
                        ),
                    )
                ),
                rangeProvider = CartesianLayerRangeProvider.auto(),
            ),
            startAxis = VerticalAxis
                .rememberStart(
                    valueFormatter = startAxisFormatter,
                    label = label,
                    tick = null,
                    title = startAxisTitle,
                    titleComponent = rememberTextComponent(),
                    itemPlacer = remember {
                        VerticalAxis.ItemPlacer.step({ extraStore ->
                            extraStore.getOrNull(yStepExtraKey)
                        })
                    },
                    guideline = solidGuideline
                ),
            bottomAxis = HorizontalAxis.rememberBottom(
                guideline = solidGuideline,
                label = xLabel,
                valueFormatter = CartesianValueFormatter { _, value, _ ->
                    val formatted = DecimalFormat("#.##").format(value)
                    "$formatted km"
                },
            ),
            endAxis = VerticalAxis.rememberEnd(
                tick = null,
                label = null,
                guideline = null

            ),
            topAxis = HorizontalAxis.rememberTop(
                tick = null,
                label = null
            ),
            marker = marker,
            persistentMarkers = persistentMarker,
            markerVisibilityListener = markerVisibilityListener,
            decorations = avgValue?.let { listOf(rememberHorizontalLine(avgValue = it)) }
                ?: emptyList(),
            getXStep = { model ->
                model.extraStore.getOrNull(xStepExtraKey) ?: model.getXDeltaGcd()
            },
            layerPadding = { extraStore ->
                cartesianLayerPadding(
                    unscalableStart = 4.dp,
                    unscalableEnd = 4.dp,
                    scalableStart = 4.dp,
                    scalableEnd = 4.dp
                )
            }
        ),
        modelProducer,
        modifier.offset(y = (-24).dp),
        rememberVicoScrollState(scrollEnabled = false),
        animationSpec = null,
    )
}

@Composable
private fun rememberHorizontalLine(avgValue: Double): HorizontalLine {
    val fill = fill(Color.Black)
    val line = rememberLineComponent(
        fill = fill,
        thickness = 1.dp,
        shape = dashedShape(gapLength = 3.dp, dashLength = 6.dp)
    )

    return remember {
        HorizontalLine(
            y = { avgValue },
            line = line,
        )
    }
}

@Composable
internal fun rememberMarker(
    valueFormatter: DefaultCartesianMarker.ValueFormatter =
        DefaultCartesianMarker.ValueFormatter.default(),
    color: Color
): CartesianMarker {
    val labelBackgroundShape = markerCorneredShape(CorneredShape.rounded(8.dp))
    val labelBackground =
        rememberShapeComponent(
            fill = fill(color),
            shape = labelBackgroundShape,
        )
    val label =
        rememberTextComponent(
            textAlignment = Layout.Alignment.ALIGN_NORMAL,
            padding = insets(10.dp, 10.dp),
            background = labelBackground,
            minWidth = TextComponent.MinWidth.fixed(80F),
            textSize = 14.sp,
            lineCount = 2,
            color = Color.White
        )

    val indicatorFrontComponent =
        rememberShapeComponent(fill(StrideTheme.colorScheme.surface), CorneredShape.Pill)
    val guideline = rememberAxisGuidelineComponent(
        shape = Shape.Rectangle, fill = fill(Color.Black),
        thickness = 2.dp
    )
    return rememberDefaultCartesianMarker(
        label = label,
        valueFormatter = valueFormatter,
        indicator =
        { _ ->
            val markerColor = Color.Black
            LayeredComponent(
                back = ShapeComponent(fill(markerColor.copy(alpha = 0.15f)), CorneredShape.Pill),
                front =
                LayeredComponent(
                    back = ShapeComponent(
                        fill = fill(markerColor),
                        shape = CorneredShape.Pill
                    ),
                    front = indicatorFrontComponent,
                    padding = insets(3.dp),
                ),
                padding = insets(3.dp),
            )
        },
        indicatorSize = 12.dp,
        guideline = guideline,
    )
}

fun calculateNiceStep(min: Float, max: Float, maxLabels: Int = 7): Float {
    val range = max - min
    if (range == 0f) return 1f

    val approxStep = range / (maxLabels - 1)
    val magnitude = 10.0.pow(floor(log10(approxStep.toDouble())))
    val residual = approxStep / magnitude

    val niceResidual = when {
        residual < 1.5 -> 1.0
        residual < 3.0 -> 2.0
        residual < 7.0 -> 5.0
        else -> 10.0
    }

    return (niceResidual * magnitude).toFloat()
}
