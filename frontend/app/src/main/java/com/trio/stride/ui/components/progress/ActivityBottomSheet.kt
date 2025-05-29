package com.trio.stride.ui.components.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.trio.stride.R
import com.trio.stride.data.remote.dto.ProgressActivityDto
import com.trio.stride.domain.model.ProgressActivity
import com.trio.stride.ui.components.LoadingLarger
import com.trio.stride.ui.components.activity.detail.StatText
import com.trio.stride.ui.screens.progress.detail.LoadingState
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.formatDuration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityBottomSheet(
    item: ProgressActivityDto?,
    uiState: LoadingState,
    sportImage: String?,
    title: String,
    onItemSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = StrideTheme.colors.white,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                title,
                style = StrideTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            when (uiState) {
                LoadingState.Loading -> {
                    Spacer(Modifier.height(24.dp))
                    LoadingLarger()
                    Spacer(Modifier.height(24.dp))
                }

                LoadingState.Idle -> {
                    if (item?.activities != null) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            item {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .background(StrideTheme.colorScheme.background)
                                        .padding(vertical = 24.dp, horizontal = 24.dp),
                                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                                ) {
                                    if (item.distance != null) {
                                        StatText("Distance", "${item.distance} km")
                                    }
                                    if (item.elevation != null) {
                                        StatText("Elev Gain", "${item.elevation} m")
                                    }
                                    if (item.time != null) {
                                        StatText("Time", "${formatDuration(item.time)} km")
                                    }
                                }
                            }
                            itemsIndexed(item.activities) { _, item ->
                                ProgressActivityView(
                                    item = item,
                                    sportImage = sportImage,
                                    onItemSelected
                                )
                            }
                        }
                    }
                }

                is LoadingState.Error -> {
                    Text(
                        uiState.message,
                        style = StrideTheme.typography.titleMedium,
                        color = StrideTheme.colors.red700
                    )
                }
            }
        }
    }
}

@Composable
fun ProgressActivityView(
    item: ProgressActivity,
    sportImage: String?,
    onItemClick: (String) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable { onItemClick(item.id) }
        .padding(vertical = 16.dp, horizontal = 24.dp)) {
        Row {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.mapImage)
                    .error(R.drawable.image_icon)
                    .fallback(R.drawable.image_icon)
                    .placeholder(R.drawable.image_icon)
                    .crossfade(true)
                    .build(),
                contentDescription = "image",
                modifier = Modifier
                    .width(50.dp)
                    .aspectRatio(1f / 1f)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )
            Spacer(Modifier.width(12.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(sportImage)
                                .error(R.drawable.image_icon)
                                .fallback(R.drawable.image_icon)
                                .placeholder(R.drawable.image_icon)
                                .crossfade(true)
                                .build()
                        ),
                        contentDescription = "Sport Icon",
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        item.name,
                        maxLines = 1,
                        style = StrideTheme.typography.titleMedium,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            if (item.distance != null) {
                StatText("Distance", "${item.distance} km")
            }
            if (item.elevation != null) {
                StatText("Elev Gain", "${item.elevation} m")
            }
            if (item.time != null) {
                StatText("Time", "${formatDuration(item.time)} km")
            }
        }
    }

}