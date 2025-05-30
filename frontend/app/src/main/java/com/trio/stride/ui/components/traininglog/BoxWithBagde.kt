package com.trio.stride.ui.components.traininglog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trio.stride.R
import com.trio.stride.ui.theme.StrideTheme

@Composable
fun BoxWithBadge(
    color: Color,
    numOfActivities: Int,
) {
    val textNumber = if (numOfActivities < 10) numOfActivities.toString() else "9+"
    Box(
        modifier = Modifier.size(28.dp)
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(
                    color,
                    CircleShape
                )
                .align(Alignment.Center)
        )

        if (numOfActivities > 1) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.TopEnd)
                    .background(StrideTheme.colorScheme.onSurface, CircleShape),
                Alignment.Center
            ) {
                Text(
                    text = textNumber,
                    color = StrideTheme.colorScheme.inverseOnSurface,
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.roboto_light)),
                        fontSize = 10.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}