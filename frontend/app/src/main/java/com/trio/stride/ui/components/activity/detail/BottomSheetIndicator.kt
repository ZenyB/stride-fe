package com.trio.stride.ui.components.activity.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.trio.stride.ui.theme.StrideTheme

@Composable
fun BottomSheetIndicator(
    modifier: Modifier = Modifier,
    width: Dp = 32.dp,
    height: Dp = 4.dp,
    shape: Shape = MaterialTheme.shapes.extraLarge,
    color: Color = StrideTheme.colorScheme.outline,
) {
    Surface(
        modifier =
        modifier
            .padding(vertical = 8.dp),
        color = color,
        shape = shape
    ) {
        Box(Modifier.size(width = width, height = height))
    }
}