package com.trio.stride.ui.components.activity.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.trio.stride.ui.theme.StrideTheme

@Composable
fun StatRow(title: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            title,
            style = StrideTheme.typography.bodyLarge.copy(
                color = StrideTheme.colors.gray600,
                fontWeight = FontWeight.Light
            )
        )
        Text(
            value,
            style = StrideTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
        )
    }
}