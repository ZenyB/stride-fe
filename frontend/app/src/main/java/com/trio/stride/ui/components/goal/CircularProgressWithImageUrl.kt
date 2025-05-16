package com.trio.stride.ui.components.goal

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun CircularProgressWithImageUrl(
    percentage: Float,
    imageUrl: String,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 4.dp,
    progressColor: Color = Color(0xFF33D03C),
    backgroundColor: Color = Color.LightGray
) {
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            drawArc(
                color = backgroundColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = stroke
            )
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = 360 * percentage,
                useCenter = false,
                style = stroke
            )
        }

        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }
}
