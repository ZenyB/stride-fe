package com.trio.stride.ui.components.activity.detail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.trio.stride.domain.model.HeartRateInfo
import com.trio.stride.domain.usecase.activity.redShades
import com.trio.stride.ui.utils.formatDuration
import kotlin.math.pow
import kotlin.math.sqrt

private const val TOTAL_ANGLE = 360.0f
private val STROKE_SIZE_UNSELECTED = 50.dp
private val STROKE_SIZE_SELECTED = 58.dp

data class DonutChartDataCollection(
    var items: List<HeartRateInfo>
) {
    internal var totalDuration: Long = items.sumOf { it.value }
        private set
}

private data class DrawingAngles(val start: Float, val end: Float) {
    val centerAngle: Float get() = start + end / 2
}

private fun DrawingAngles.isInsideAngle(angle: Float) =
    angle > this.start && angle < this.start + this.end

private class DonutChartState(
    val state: State = State.Unselected
) {
    val stroke: Dp
        get() = when (state) {
            State.Selected -> STROKE_SIZE_SELECTED
            State.Unselected -> STROKE_SIZE_UNSELECTED
        }

    enum class State {
        Selected, Unselected
    }
}

@Composable
fun DonutChart(
    modifier: Modifier = Modifier,
    chartSize: Dp = 350.dp,
    data: DonutChartDataCollection,
    gapPercentage: Float = 0.02f,
    selectedIndex: MutableState<Int>,
    previousSelected: MutableState<Int>,
    selectionView: @Composable (selectedItem: HeartRateInfo?) -> Unit = {},
) {
    val animationTargetState = (0..data.items.size).map {
        remember { mutableStateOf(DonutChartState()) }
    }


//    val animValues = (0..data.items.size).map {
//        animateDpAsState(
//            targetValue = animationTargetState[it].value.stroke,
//            animationSpec = TweenSpec(300)
//        )
//    }
    val anglesList: MutableList<DrawingAngles> = remember { mutableListOf() }
    val gapAngle = data.calculateGapAngle(gapPercentage)
    var center = Offset(0f, 0f)

    LaunchedEffect(selectedIndex.value) {
        if (selectedIndex.value >= 0) {
            animationTargetState[selectedIndex.value].value = DonutChartState(
                DonutChartState.State.Selected
            )
        }
    }

    LaunchedEffect(previousSelected.value) {
        if (previousSelected.value >= 0) {
            animationTargetState[previousSelected.value].value = DonutChartState(
                DonutChartState.State.Unselected
            )
        }
    }


    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(
            modifier = Modifier
                .size(chartSize)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { tapOffset ->
                            handleCanvasTap(
                                center = center,
                                tapOffset = tapOffset,
                                anglesList = anglesList,
                                currentSelectedIndex = selectedIndex.value,
                                currentStrokeValues = animationTargetState.map { it.value.stroke.toPx() },
                                onItemSelected = { index ->
                                    selectedIndex.value = index
//                                    animationTargetState[index].value = DonutChartState(
//                                        DonutChartState.State.Selected
//                                    )
                                },
                                onItemDeselected = { index ->
                                    animationTargetState[index].value = DonutChartState(
                                        DonutChartState.State.Unselected
                                    )
                                },
                                onNoItemSelected = {
                                    selectedIndex.value = -1
                                }
                            )
                        }
                    )
                },
            onDraw = {
                val defaultStrokeWidth = STROKE_SIZE_UNSELECTED.toPx()
                center = this.center
                anglesList.clear()
                var lastAngle = 0f
                data.items.forEachIndexed { ind, item ->
                    val sweepAngle = data.findSweepAngle(ind, gapPercentage)
                    anglesList.add(DrawingAngles(lastAngle, sweepAngle))
//                    val strokeWidth = animValues[ind].value.toPx()
                    val strokeWidth = animationTargetState[ind].value.stroke.toPx()

                    drawArc(
                        color = item.color ?: redShades[0],
                        startAngle = lastAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(defaultStrokeWidth / 2, defaultStrokeWidth / 2),
                        style = Stroke(strokeWidth, cap = StrokeCap.Butt),
                        size = Size(
                            size.width - defaultStrokeWidth,
                            size.height - defaultStrokeWidth
                        )
                    )
                    lastAngle += sweepAngle + gapAngle
                }
                if (selectedIndex.value >= 0) {
                    val selectedAngle = anglesList[selectedIndex.value].centerAngle
                    val extraOffset = 4.dp.toPx()
                    val outerRadius = size.width / 2f
                    val tooltipRadius = outerRadius + extraOffset
                    val radians = Math.toRadians(selectedAngle.toDouble())
                    val x = center.x + tooltipRadius * kotlin.math.cos(radians).toFloat()
                    val y = center.y + tooltipRadius * kotlin.math.sin(radians).toFloat()

                    val labelText = formatDuration(data.items[selectedIndex.value].value)
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 36f
                        textAlign = android.graphics.Paint.Align.CENTER
                        isAntiAlias = true
                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                    }
                    val padding = 32f
                    val textWidth = paint.measureText(labelText) + padding * 2
                    val textHeight = paint.descent() - paint.ascent() + padding
                    drawContext.canvas.nativeCanvas.drawRoundRect(
                        x - textWidth / 2 + 4f,
                        y - textHeight / 2 + 4f,
                        x + textWidth / 2 + 4f,
                        y + textHeight / 2 + 4f,
                        12f, 12f,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.GRAY
                            style = android.graphics.Paint.Style.FILL
                            isAntiAlias = true
                            alpha = 70
                        }
                    )

                    drawContext.canvas.nativeCanvas.drawRoundRect(
                        x - textWidth / 2,
                        y - textHeight / 2,
                        x + textWidth / 2,
                        y + textHeight / 2,
                        12f, 12f,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.WHITE
                            style = android.graphics.Paint.Style.FILL
                            isAntiAlias = true
                        }
                    )
                    drawContext.canvas.nativeCanvas.drawText(
                        labelText,
                        x,
                        y + paint.textSize / 2,
                        paint
                    )
                }
            }
        )
        selectionView(if (selectedIndex.value >= 0) data.items[selectedIndex.value] else null)
    }
}

private fun handleCanvasTap(
    center: Offset,
    tapOffset: Offset,
    anglesList: List<DrawingAngles>,
    currentSelectedIndex: Int,
    currentStrokeValues: List<Float>,
    onItemSelected: (Int) -> Unit = {},
    onItemDeselected: (Int) -> Unit = {},
    onNoItemSelected: () -> Unit = {},
) {
    val normalized = tapOffset.findNormalizedPointFromTouch(center)
    val touchAngle =
        calculateTouchAngleAccordingToCanvas(center, normalized)
    val distance = findTouchDistanceFromCenter(center, normalized)

    var selectedIndex = -1
    var newDataTapped = false

    anglesList.forEachIndexed { ind, angle ->
        val stroke = currentStrokeValues[ind]
        if (angle.isInsideAngle(touchAngle)) {
            if (distance > (center.x - stroke) &&
                distance < (center.x)
            ) { // since it's a square center.x or center.y will be the same
                selectedIndex = ind
                newDataTapped = true
            }
        }
    }

    if (selectedIndex >= 0 && newDataTapped) {
        onItemSelected(selectedIndex)
    }
    if (currentSelectedIndex >= 0) {
        onItemDeselected(currentSelectedIndex)
        if (currentSelectedIndex == selectedIndex || !newDataTapped) {
            onNoItemSelected()
        }
    }
}

/**
 * Find the distance based on two points in a graph. Calculated using the pythagorean theorem.
 */
private fun findTouchDistanceFromCenter(center: Offset, touch: Offset) =
    sqrt((touch.x - center.x).pow(2) + (touch.y - center.y).pow(2))

/**
 * The touch point start from Canvas top left which ranges from (0,0) -> (canvas.width, canvas.height).
 * We need to normalize this point so that it's based on the canvas center instead.
 */
private fun Offset.findNormalizedPointFromTouch(canvasCenter: Offset) =
    Offset(this.x, canvasCenter.y + (canvasCenter.y - this.y))

/**
 * Calculate the touch angle based on the canvas center. Then adjust the angle so that
 * drawing starts from the 4th quadrant instead of the first.
 */
private fun calculateTouchAngleAccordingToCanvas(
    canvasCenter: Offset,
    normalizedPoint: Offset
): Float {
    val angle = calculateTouchAngleInDegrees(canvasCenter, normalizedPoint)
    return adjustAngleToCanvas(angle).toFloat()
}

/**
 * Calculate touch angle in radian using atan2(). Afterwards, convert the radian to degrees to be
 * compared to other data points.
 */
private fun calculateTouchAngleInDegrees(canvasCenter: Offset, normalizedPoint: Offset): Double {
    val touchInRadian = kotlin.math.atan2(
        normalizedPoint.y - canvasCenter.y,
        normalizedPoint.x - canvasCenter.x
    )
    return touchInRadian * -180 / Math.PI // Convert radians to angle in degrees
}

/**
 * Start from 4th quadrant going to 1st quadrant, degrees ranging from 0 to 360
 */
private fun adjustAngleToCanvas(angle: Double) = (angle + TOTAL_ANGLE) % TOTAL_ANGLE

/**
 * Calculate the gap width between the arcs based on [gapPercentage]. The percentage is applied
 * to the average count to determine the width in pixels.
 */
private fun DonutChartDataCollection.calculateGap(gapPercentage: Float): Float {
    if (this.items.isEmpty()) return 0f

    return (this.totalDuration / this.items.size) * gapPercentage
}

/**
 * Returns the total data points including the individual gap widths indicated by the
 * [gapPercentage].
 */
private fun DonutChartDataCollection.getTotalAmountWithGapIncluded(gapPercentage: Float): Float {
    val gap = this.calculateGap(gapPercentage)
    return this.totalDuration + (this.items.size * gap)
}

/**
 * Calculate the sweep angle of an arc including the gap as well. The gap is derived based
 * on [gapPercentage].
 */
private fun DonutChartDataCollection.calculateGapAngle(gapPercentage: Float): Float {
    val gap = this.calculateGap(gapPercentage)
    val totalAmountWithGap = this.getTotalAmountWithGapIncluded(gapPercentage)

    return (gap / totalAmountWithGap) * TOTAL_ANGLE
}

/**
 * Returns the sweep angle of a given point in the [DonutChartDataCollection]. This calculations
 * takes the gap between arcs into the account.
 */
private fun DonutChartDataCollection.findSweepAngle(
    index: Int,
    gapPercentage: Float
): Float {
    val amount = items[index].value
    val gap = this.calculateGap(gapPercentage)
    val totalWithGap = getTotalAmountWithGapIncluded(gapPercentage)
    val gapAngle = this.calculateGapAngle(gapPercentage)
    return ((((amount + gap) / totalWithGap) * TOTAL_ANGLE)) - gapAngle
}
