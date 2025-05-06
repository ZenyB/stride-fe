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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.trio.stride.R
import com.trio.stride.domain.model.RouteItem
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.formatDuration

@Composable
fun RouteItemDetail(item: RouteItem) {
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
                "${item.avgDistance} km • ${formatDuration(item.avgTime)}",
                style = StrideTheme.typography.bodyLarge
                    .copy(fontWeight = FontWeight.Thin),
            )
            Text(
                item.location,
                style = StrideTheme.typography.bodyLarge
                    .copy(fontWeight = FontWeight.Thin),
            )
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(3) { imageUrl ->
                AsyncImage(
                    model = "https://img.freepik.com/free-photo/low-rise-building_1127-3272.jpg?t=st=1745483374~exp=1745486974~hmac=479952fdec79f12dc1585e2f2f74fdec391e62bb62a4b03c49de54df479329bf&w=996",
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
                onClick = {},
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
                onClick = {},
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

@Preview(showBackground = true)
@Composable
fun PreviewDetail() {
    IconButton(
        onClick = {
        },
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = "Back",
            modifier = Modifier.size(24.dp)
        )
    }
}