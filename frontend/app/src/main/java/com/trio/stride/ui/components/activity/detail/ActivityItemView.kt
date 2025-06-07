package com.trio.stride.ui.components.activity.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.trio.stride.R
import com.trio.stride.domain.model.ActivityItem
import com.trio.stride.domain.model.SportMapType
import com.trio.stride.ui.components.Avatar
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.formatDate
import com.trio.stride.ui.utils.formatDuration

@Composable
fun ActivityItemView(item: ActivityItem, onClick: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier
            .background(StrideTheme.colorScheme.surface)
            .fillMaxWidth()
            .clickable {
                onClick(item.id)
            }
            .padding(16.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Avatar(
                ava = item.user.ava,
                name = item.user.name
            )

            Column {
                Text(item.user.name, style = StrideTheme.typography.titleMedium)
                Text(
                    formatDate(item.createdAt),
                    style = StrideTheme.typography.bodySmall,
                    color = StrideTheme.colors.gray600
                )
            }
        }

        Text(
            item.name,
            style = StrideTheme.typography.titleLarge
                .copy(fontWeight = FontWeight.Bold)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            if (item.totalDistance != null && item.totalDistance > 0) {
                StatText("Distance", "${item.totalDistance} km")

            }
            if (item.movingTimeSeconds != null && item.movingTimeSeconds > 0) {
                StatText("Time", formatDuration(item.movingTimeSeconds))

            }
            if (item.elevationGain != null && item.elevationGain > 0) {
                StatText("Elevation Gain", "${item.elevationGain}m")

            }
        }

        if (item.sport.sportMapType != SportMapType.NO_MAP) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.mapImage)
                    .crossfade(true)
                    .placeholder(R.drawable.image_icon)
                    .error(R.drawable.image_icon)
                    .fallback(R.drawable.image_icon)
                    .build(),
                contentDescription = "activity",

                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(12f / 8f),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )
        }
    }
}

@Composable
fun StatText(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    horizontal: Alignment.Horizontal = Alignment.Start
) {
    Column(modifier, horizontalAlignment = horizontal) {
        Text(
            title,
            style = StrideTheme.typography.bodyMedium,
            color = StrideTheme.colors.gray600
        )
        Text(
            value, style = StrideTheme
                .typography.titleMedium.copy(fontSize = 18.sp)
        )
    }
}