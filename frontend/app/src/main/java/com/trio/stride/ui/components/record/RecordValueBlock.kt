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
import com.trio.stride.ui.theme.StrideTheme

enum class RecordValueBlockType {
    Large,
    Small
}

@Composable
fun RecordValueBlock(
    title: String,
    value: String? = null,
    modifier: Modifier = Modifier,
    type: RecordValueBlockType = RecordValueBlockType.Small,
    unit: String? = null,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))
        Text(
            title.uppercase(),
            style = StrideTheme.typography.labelMedium,
            color = StrideTheme.colors.gray
        )
        Text(
            value ?: "--",
            style = if (type == RecordValueBlockType.Large)
                StrideTheme.typography.headlineSmall
            else
                StrideTheme.typography.headlineLarge,
            color = StrideTheme.colorScheme.onBackground
        )
        unit?.let { it ->
            Text(
                it,
                style = StrideTheme.typography.headlineSmall,
                color = StrideTheme.colorScheme.onBackground
            )
        }
        Spacer(Modifier.height(48.dp))
    }
}