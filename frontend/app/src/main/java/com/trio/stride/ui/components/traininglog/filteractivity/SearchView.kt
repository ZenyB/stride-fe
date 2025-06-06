package com.trio.stride.ui.components.traininglog.filteractivity

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.trio.stride.ui.theme.StrideTheme

@Composable
fun SearchView(
    query: String,
    onQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        textStyle = StrideTheme.typography.headlineSmall,
        onValueChange = { onQueryChanged(it) },
        modifier = modifier
            .fillMaxWidth(),
        placeholder = {
            Text(
                "Search by keyword",
                style = StrideTheme.typography.headlineSmall.copy(StrideTheme.colors.placeHolderText)
            )
        },
        singleLine = true,
        trailingIcon = {
            IconButton(
                onClick = { onQueryChanged("") }
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Clear query",
                    modifier = Modifier.size(24.dp),
                    tint = StrideTheme.colorScheme.onSurface
                )
            }
        }
    )
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    val query = remember { mutableStateOf("") }
    SearchView(
        query = query.value,
        onQueryChanged = { query.value = it }
    )
}