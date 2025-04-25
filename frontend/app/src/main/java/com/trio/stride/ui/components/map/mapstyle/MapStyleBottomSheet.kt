package com.trio.stride.ui.components.map.mapstyle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mapbox.maps.Style
import com.trio.stride.R
import com.trio.stride.domain.model.MapStyleItem
import com.trio.stride.ui.components.map.MapRadioButton
import com.trio.stride.ui.theme.StrideTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapStyleBottomSheet(
    mapStyle: String,
    onMapStyleSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    val options = listOf(
        MapStyleItem(Style.MAPBOX_STREETS, "Standard", R.drawable.ic_map_standard),
        MapStyleItem(Style.SATELLITE, "Satellite", R.drawable.ic_map_satellite),
        MapStyleItem(Style.SATELLITE_STREETS, "Hybrid", R.drawable.ic_map_hybrid),
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = StrideTheme.colors.white,
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text("Map Types", style = StrideTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                itemsIndexed(options) { _, option ->
                    MapRadioButton(
                        text = option.label,
                        imageResId = option.imageResId,
                        selected = option.style == mapStyle,
                        onClick = {
                            onMapStyleSelected(option.style)
                        }
                    )
                }
            }
        }
    }
}

