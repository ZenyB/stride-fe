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
    Small
}

@Composable
fun RecordValueBlock(
    title: String,
    modifier: Modifier = Modifier,
    value: String? = null,
    type: RecordValueBlockType = RecordValueBlockType.Small,
    unit: String? = null,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        Text(
            title.uppercase(),
            style = StrideTheme.typography.labelLarge,
            color = StrideTheme.colors.gray
        )
        Text(
            value ?: "--",
            style = if (type == RecordValueBlockType.Large)
                StrideTheme.typography.displayLarge.copy(fontSize = 120.sp)
            else
                StrideTheme.typography.displaySmall.copy(fontSize = 60.sp),
            color = StrideTheme.colorScheme.onBackground
        )
        unit?.let { it ->
            Text(
                it,
                style = StrideTheme.typography.labelLarge,
                color = StrideTheme.colorScheme.onBackground
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}