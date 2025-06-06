package com.trio.stride.ui.components.traininglog.filteractivity

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.trio.stride.ui.theme.StrideTheme

@Composable
fun FilterChip(
    contentText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    active: Boolean = false,
    trailingIcons: @Composable (() -> Unit)? = null,
    leadingIcons: @Composable (() -> Unit)? = null,
) {
    val borderColor = if (active) StrideTheme.colorScheme.primary else StrideTheme.colors.grayBorder
    val color =
        if (active) StrideTheme.colorScheme.primary else StrideTheme.colorScheme.onSurface
    val textStyle = StrideTheme.typography.labelMedium

    Box(
        modifier
            .clip(RoundedCornerShape(8.dp))
            .border(width = 1.dp, color = borderColor, RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true)
            ) {
                onClick()
            }
    ) {
        CompositionLocalProvider(LocalContentColor provides color) {
            Row(
                Modifier.padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                trailingIcons?.let { it() }
                Text(contentText, style = textStyle, color = color)
                leadingIcons?.let { it() }
            }
        }
    }
}