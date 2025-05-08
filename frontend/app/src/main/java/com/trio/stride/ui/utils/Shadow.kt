package com.trio.stride.ui.utils

import android.os.Build
import android.os.Build.VERSION_CODES.Q
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.trio.stride.ui.theme.StrideColor

fun Modifier.advancedShadow(
    color: Color = StrideColor.gray,
    alpha: Float = 0.5f,
    cornersRadius: Dp = 0.dp,
    shadowBlurRadius: Dp = 10.dp,
    offsetY: Dp = 2.dp,
    offsetX: Dp = 0.dp
): Modifier = if (Build.VERSION.SDK_INT >= Q) {
    drawBehind {
        val shadowColor = color.copy(alpha = alpha).toArgb()
        drawIntoCanvas {
            val paint = Paint().apply {
                asFrameworkPaint().apply {
                    this.color = shadowColor
                    setShadowLayer(
                        shadowBlurRadius.toPx(),
                        offsetX.toPx(),
                        offsetY.toPx(),
                        shadowColor
                    )
                }
            }
            it.drawRoundRect(
                0f,
                0f,
                size.width,
                size.height,
                cornersRadius.toPx(),
                cornersRadius.toPx(),
                paint
            )
        }
    }
} else {
    Modifier
}
