package com.trio.stride.ui.components.map.routesheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.trio.stride.R
import com.trio.stride.domain.model.RouteItem
import com.trio.stride.domain.model.toFormattedString
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.formatDuration

@Composable
fun RouteItemDetail(item: RouteItem, onSaveRoute: () -> Unit, startRecord: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            item.name,
            style = StrideTheme.typography.headlineSmall,
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                "${item.avgDistance} km • ${formatDuration(item.avgTime.toLong())}",
                style = StrideTheme.typography.bodyLarge
                    .copy(fontWeight = FontWeight.Thin),
            )
            Text(
                item.location.toFormattedString(),
                style = StrideTheme.typography.bodyLarge
                    .copy(fontWeight = FontWeight.Thin),
            )
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(
                item.images ?: emptyList()
            ) { _, imgUrl ->
                AsyncImage(
                    model = imgUrl,
                    contentDescription = "route",
                    modifier = Modifier
                        .width(300.dp)
                        .aspectRatio(12f / 7f)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center
                )
            }
            items(1) { _ ->
                AsyncImage(
                    model = item.mapImage,
                    contentDescription = "route",
                    modifier = Modifier
                        .width(300.dp)
                        .aspectRatio(12f / 7f)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = onSaveRoute,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = StrideTheme.colors.gray200
                ),
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_save),
                    contentDescription = "icon",
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(32.dp))
            IconButton(
                onClick = {},
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = StrideTheme.colors.gray200
                ),
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_share),
                    contentDescription = "icon",
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(32.dp))
            IconButton(
                onClick = { startRecord(item.geometry) },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = StrideTheme.colors.gray200
                ),
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            ) {
                Icon(
                    painter = painterResource(R.drawable.record),
                    contentDescription = "icon",
                    tint = Color.Black
                )
            }
        }

    }
}