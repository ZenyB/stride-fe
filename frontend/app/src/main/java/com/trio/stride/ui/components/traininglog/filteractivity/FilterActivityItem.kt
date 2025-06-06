package com.trio.stride.ui.components.traininglog.filteractivity

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.trio.stride.domain.model.ActivityItem
import com.trio.stride.domain.model.ActivityUser
import com.trio.stride.domain.model.Sport
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.formatDate
import com.trio.stride.ui.utils.formatDistance
import com.trio.stride.ui.utils.formatTimeHMS
import com.trio.stride.ui.utils.getStartOfWeekInMillis

@Composable
fun FilterActivityItem(
    activity: ActivityItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple()
        ) {
            onClick()
        }
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp)),
                model = activity.mapImage,
                contentDescription = "Activity Image"
            )
            Spacer(Modifier.width(12.dp))
            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    activity.name,
                    style = StrideTheme.typography.titleLarge,
                    color = StrideTheme.colorScheme.onSurface
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        modifier = Modifier.size(16.dp),
                        model = activity.sport.image,
                        contentDescription = "Sport Icon"
                    )
                    Spacer(Modifier.width(8.dp))
                    if (activity.sport.sportMapType == null) {
                        Text(
                            formatTimeHMS(activity.movingTimeSeconds?.toInt() ?: 0),
                            style = StrideTheme.typography.labelLarge,
                            color = StrideTheme.colors.gray
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                "${formatDistance(activity.totalDistance ?: 0.0)} km",
                                style = StrideTheme.typography.labelLarge,
                                color = StrideTheme.colors.gray
                            )
                            Box(
                                Modifier
                                    .size(1.dp)
                                    .background(StrideTheme.colors.gray, CircleShape)
                            )
                            Text(
                                "${activity.elevationGain} m",
                                style = StrideTheme.typography.labelLarge,
                                color = StrideTheme.colors.gray
                            )
                            Box(
                                Modifier
                                    .size(1.dp)
                                    .background(StrideTheme.colors.gray, CircleShape)
                            )
                            Text(
                                formatTimeHMS(activity.movingTimeSeconds?.toInt() ?: 0),
                                style = StrideTheme.typography.labelLarge,
                                color = StrideTheme.colors.gray
                            )
                        }
                    }
                }
                Text(
                    formatDate(activity.createdAt),
                    style = StrideTheme.typography.labelLarge,
                    color = StrideTheme.colors.gray
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    FilterActivityItem(
        activity = ActivityItem(
            id = "1",
            name = "Night Ride",
            sport = Sport(
                name = "ABC",
                image = "https://pglijwfxeearqkhmpsdq.supabase.co/storage/v1/object/public/users//15f639c2-5679-40ef-baa2-3cba4af77757.jpg"
            ),
            totalDistance = 242.5,
            elevationGain = 2,
            movingTimeSeconds = 2141,
            mapImage = "https://pglijwfxeearqkhmpsdq.supabase.co/storage/v1/object/public/users/452e9377-3163-4038-a4bf-4f0b76a23286.png",
            createdAt = getStartOfWeekInMillis(),
            user = ActivityUser()
        )
    ) {}
}