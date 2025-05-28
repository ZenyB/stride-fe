package com.trio.stride.ui.screens.progress

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.trio.stride.R
import com.trio.stride.domain.model.Sport
import com.trio.stride.ui.theme.StrideTheme
import java.util.Locale

@Composable
fun SportHorizontalList(
    items: List<Sport>,
    selectedId: String,
    onOptionSelected: (id: String) -> Unit
) {
    val primaryColor = StrideTheme.colorScheme.primary
    val unselectedColor = StrideTheme.colorScheme.outline

    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items.forEach { option ->
            val isSelected = option.id == selectedId

            OutlinedButton(
                onClick = { onOptionSelected(option.id) },
                border = BorderStroke(1.dp, if (isSelected) primaryColor else unselectedColor),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (isSelected) primaryColor else unselectedColor
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minWidth = 64.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(option.image)
                                .error(R.drawable.image_icon)
                                .fallback(R.drawable.image_icon)
                                .placeholder(R.drawable.image_icon)
                                .crossfade(true)
                                .build()
                        ),
                        contentDescription = "Sport Icon",
                        modifier = Modifier.size(20.dp),
                        tint = if (isSelected) primaryColor else unselectedColor

                    )
                    Text(
                        option.name.lowercase(Locale.ROOT).replaceFirstChar { c -> c.uppercase() },
                        style = StrideTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SportRadioButton() {
    SportHorizontalList(
        items = listOf(
            Sport(id = "1", name = "Ride"),
            Sport(name = "Pilates"),
            Sport(),
            Sport(),
            Sport()
        ),
        selectedId = "1"
    ) {}
}