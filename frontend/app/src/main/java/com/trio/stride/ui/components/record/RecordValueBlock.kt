package com.trio.stride.ui.components.record

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trio.stride.ui.theme.StrideTheme

enum class RecordValueBlockType {
    Large,
    Small,
    OnMapSmall,
    OnMapLarge
}

@Composable
fun RecordValueBlock(
    title: String,
    modifier: Modifier = Modifier,
    value: String? = null,
    type: RecordValueBlockType = RecordValueBlockType.Small,
    unit: String? = null,
) {
    val textSpacing = when (type) {
        RecordValueBlockType.Small, RecordValueBlockType.Large -> 16.dp
        else -> 8.dp
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(textSpacing),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val newTitle =
            if ((type == RecordValueBlockType.OnMapSmall || type == RecordValueBlockType.OnMapLarge) && unit != null)
                "${title.uppercase()} (${unit.lowercase()})"
            else title.uppercase()
        when (type) {
            RecordValueBlockType.Small, RecordValueBlockType.Large -> Spacer(Modifier.height(16.dp))
            else -> {}
        }
        Text(
            newTitle,
            style = StrideTheme.typography.labelLarge,
            color = StrideTheme.colors.gray
        )
        Text(
            value ?: "--",
            style = when (type) {
                RecordValueBlockType.Large -> StrideTheme.typography.displayLarge.copy(fontSize = 120.sp)
                RecordValueBlockType.Small -> StrideTheme.typography.displaySmall.copy(fontSize = 60.sp)
                RecordValueBlockType.OnMapLarge -> StrideTheme.typography.headlineLarge
                RecordValueBlockType.OnMapSmall -> StrideTheme.typography.headlineSmall
            },
            color = StrideTheme.colorScheme.onBackground
        )

        when (type) {
            RecordValueBlockType.Small, RecordValueBlockType.Large ->
                unit?.let { unit ->
                    Text(
                        unit,
                        style = StrideTheme.typography.labelLarge,
                        color = StrideTheme.colorScheme.onBackground
                    )
                }

            else -> {}
        }
        when (type) {
            RecordValueBlockType.Small, RecordValueBlockType.Large -> Spacer(Modifier.height(16.dp))
            else -> {}
        }
    }
}