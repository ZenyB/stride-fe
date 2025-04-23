package com.trio.stride.ui.components.map

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mapbox.maps.Style
import com.trio.stride.R
import com.trio.stride.domain.model.MapStyleItem
import com.trio.stride.ui.theme.StrideTheme

@Composable
fun MapRadioButton(
    text: String,
    imageResId: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentColor =
        if (selected) StrideTheme.colorScheme.primary
        else StrideTheme.colorScheme.onSurface

    val textStyle =
        if (selected) StrideTheme.typography.titleSmall
        else StrideTheme.typography.bodyMedium


    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .border(
                    width = if (selected) 2.dp else 0.dp,
                    color = if (selected) StrideTheme.colorScheme.primary
                    else Color.Transparent,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(4.dp)
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .clickable {
                        onClick()
                    },
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            style = textStyle,
            color = contentColor,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MapRadioButtonPreview() {
    val options = listOf(
        MapStyleItem(Style.MAPBOX_STREETS, "Standard", R.drawable.ic_map_standard),
        MapStyleItem(Style.SATELLITE, "Satellite", R.drawable.ic_map_satellite),
        MapStyleItem(Style.SATELLITE_STREETS, "Hybrid", R.drawable.ic_map_hybrid),
    )
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        itemsIndexed(options) { index, option ->
            MapRadioButton(
                text = option.label,
                imageResId = option.imageResId,
                selected = index == 1,
                onClick = {}
            )
        }


    }
}