package com.trio.stride.ui.components.map.routesheet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.trio.stride.domain.model.RouteItem
import com.trio.stride.domain.model.toFormattedString
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.formatDuration
import com.trio.stride.ui.utils.formatKmDistance

@Composable
fun RouteItemView(item: RouteItem, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .wrapContentHeight()
            .background(
                StrideTheme.colors.white
            )
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = item.images?.getOrNull(0) ?: item.mapImage,
            contentDescription = "route",
            modifier = Modifier
                .width(80.dp)
                .aspectRatio(3f / 4f),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .padding(end = 24.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                item.name,
                maxLines = 1,
                style = StrideTheme.typography.titleMedium,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                "${formatKmDistance(item.avgDistance)} km • ${formatDuration(item.avgTime.toLong())}",
                style = StrideTheme.typography.bodySmall
                    .copy(fontWeight = FontWeight.Light),
                color = StrideTheme.colors.gray600,
            )
            Text(
                item.location.toFormattedString(),
                maxLines = 1,
                style = StrideTheme.typography.bodySmall
                    .copy(fontWeight = FontWeight.Thin),
                color = StrideTheme.colors.gray600,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}