package com.trio.stride.ui.components.traininglog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.trio.stride.R
import com.trio.stride.domain.model.TrainingLogActivity
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.formatDistance
import com.trio.stride.ui.utils.formatTimeHMS
import com.trio.stride.ui.utils.formatTimeWithDateTimestamp

@Composable
fun TrainingLogActivityItem(
    activity: TrainingLogActivity,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Icon(
            painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(activity.sport.image)
                    .error(R.drawable.image_icon)
                    .fallback(R.drawable.image_icon)
                    .placeholder(R.drawable.image_icon)
                    .crossfade(true)
                    .build()
            ),
            contentDescription = "Sport Icon",
            modifier = Modifier.size(28.dp),
            tint = StrideTheme.colorScheme.onSurface
        )
        Column(modifier = Modifier) {
            Text(
                activity.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = StrideTheme.typography.labelLarge.copy(fontSize = 20.sp),
                color = StrideTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(4.dp))
            Text(
                formatTimeWithDateTimestamp(activity.date),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = StrideTheme.typography.labelLarge,
                color = StrideTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(8.dp))
            if (activity.sport.sportMapType != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    MetricItem(
                        "Distance",
                        "${formatDistance(activity.distance.toDouble())} km"
                    )
                    MetricItem(
                        "Elevation",
                        "${activity.elevation} m"
                    )
                }
                Spacer(Modifier.height(16.dp))
            }
            MetricItem(
                "Workout Time",
                formatTimeHMS(activity.time.toInt())
            )

        }
    }
}

@Composable
private fun MetricItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            label,
            maxLines = 1,
            overflow = TextOverflow.Clip,
            style = StrideTheme.typography.labelLarge,
            color = StrideTheme.colors.placeHolderText
        )
        Text(
            value,
            maxLines = 1,
            overflow = TextOverflow.Clip,
            style = StrideTheme.typography.titleLarge,
            color = StrideTheme.colorScheme.onSurface
        )
    }
}