package com.trio.stride.ui.components.goal

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.trio.stride.domain.model.ProgressTimeRange
import com.trio.stride.ui.theme.StrideTheme
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T : Enum<T>> OutlinedRadioButtons(
    options: List<T>,
    enableOptions: List<T>? = null,
    selectedOption: T?,
    onOptionSelected: (T) -> Unit,
) {
    val primaryColor = StrideTheme.colorScheme.primary
    val unselectedColor = StrideTheme.colorScheme.outline

    FlowRow(
        modifier = Modifier
            .fillMaxWidth(),
        maxItemsInEachRow = 2,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            val isSelected = option == selectedOption

            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = { onOptionSelected(option) },
                border = BorderStroke(
                    1.dp,
                    if (isSelected) primaryColor else unselectedColor.copy(alpha = 0.5f)
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (isSelected) primaryColor else unselectedColor
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(
                    horizontal = 24.dp,
                    vertical = 12.dp
                ),
                enabled = if (enableOptions.isNullOrEmpty()) true else (enableOptions).any {
                    option.name == it.name
                }
            ) {
                Text(
                    option.name.lowercase(Locale.ROOT).replaceFirstChar { c -> c.uppercase() },
                    style = StrideTheme.typography.bodyMedium
                )
            }
        }

        if (options.size % 2 != 0) {
            Spacer(
                Modifier
                    .height(0.dp)
                    .weight(1f)
            )
        }
    }
}


@Composable
fun <T : Enum<T>> OutlinedRadioButtonsHorizontal(
    options: List<T>,
    selectedOption: T?,
    onOptionSelected: (T) -> Unit,
    content: @Composable (() -> Unit)? = null
) {
    val primaryColor = StrideTheme.colorScheme.primary
    val unselectedColor = StrideTheme.colorScheme.outline

    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (content != null) {
            content()
        }
        options.forEach { option ->
            val isSelected = option == selectedOption

            OutlinedButton(
                onClick = { onOptionSelected(option) },
                border = BorderStroke(1.dp, if (isSelected) primaryColor else unselectedColor),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (isSelected) primaryColor else unselectedColor
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(
                    horizontal = 16.dp,
                    vertical = 0.dp
                )
            ) {
                Text(
                    option.name.lowercase(Locale.ROOT).replaceFirstChar { c -> c.uppercase() },
                    style = StrideTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun <T : Enum<T>> FilledRadioButtons(
    options: List<T>,
    selectedOption: T?,
    onOptionSelected: (T) -> Unit,
) {
    val primaryColor = StrideTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        options.forEach { option ->
            val isSelected = option == selectedOption
            Button(
                modifier = Modifier.weight(1f),
                onClick = { onOptionSelected(option) },
                colors = ButtonDefaults.buttonColors(
                    contentColor = if (isSelected) StrideTheme.colorScheme.surface else StrideTheme.colorScheme.outline,
                    containerColor = if (isSelected) primaryColor else Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(
                    horizontal = 0.dp,
                    vertical = 12.dp
                )
            ) {
                Text(
                    option.toString(),
                    style = StrideTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewButton() {
    FilledRadioButtons(
        options = ProgressTimeRange.entries,
        selectedOption = ProgressTimeRange.LAST_3_MONTHS,
        onOptionSelected = {}
    )
}