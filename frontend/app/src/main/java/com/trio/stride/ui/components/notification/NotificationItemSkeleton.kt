package com.trio.stride.ui.components.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

@Composable
fun NotificationItemSkeleton(repeat: Int = 1) {
    val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.View)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .shimmer(shimmer),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repeat(repeat) {
            Row(modifier = Modifier.padding(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray, CircleShape)
                )
                Spacer(Modifier.width(16.dp))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        Modifier
                            .height(30.dp)
                            .fillMaxWidth(0.4f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.LightGray, RoundedCornerShape(4.dp))
                    )
                    Box(
                        Modifier
                            .height(80.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.LightGray, RoundedCornerShape(4.dp))
                    )
                    Box(
                        Modifier
                            .height(10.dp)
                            .fillMaxWidth(0.2f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.LightGray, RoundedCornerShape(4.dp))
                    )
                }
            }
        }
    }
}

@Composable
@Preview
private fun Preview() {
    NotificationItemSkeleton(3)
}