package com.trio.stride.ui.components.record

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.advancedShadow

@Composable
fun RecordButton(
    modifier: Modifier = Modifier,
    isPrimary: Boolean = true,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    val colors = if (isPrimary) {
        ButtonDefaults.buttonColors().copy(
            containerColor = StrideTheme.colorScheme.secondary,
            contentColor = StrideTheme.colorScheme.onSecondary
        )
    } else {
        ButtonDefaults.buttonColors().copy(
            containerColor = StrideTheme.colorScheme.surface,
            contentColor = StrideTheme.colorScheme.onSurface
        )
    }

    Box(
        modifier = modifier
            .padding(vertical = 8.dp)
            .advancedShadow(
                cornersRadius = 1000.dp,
                shadowBlurRadius = 5.dp,
            )
            .clip(CircleShape)
            .background(colors.containerColor, CircleShape)
            .size(85.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true)
            ) {
                onClick()
            },
        Alignment.Center,
    ) {
        CompositionLocalProvider(LocalContentColor provides colors.contentColor) {
            ProvideTextStyle(value = StrideTheme.typography.titleMedium) {
                Box(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                    content()
                }
            }
        }
    }
}
