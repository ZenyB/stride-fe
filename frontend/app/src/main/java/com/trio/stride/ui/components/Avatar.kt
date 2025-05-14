package com.trio.stride.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.trio.stride.R
import kotlin.math.absoluteValue

@Composable
fun Avatar(ava: Any?, name: String, width: Dp = 44.dp) {
    if (ava != null && ava.toString().isNotBlank()) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(ava)
                .error(R.drawable.user)
                .fallback(R.drawable.user)
                .placeholder(R.drawable.user)
                .crossfade(true)
                .build(),
            contentDescription = "ava",
            modifier = Modifier
                .width(width)
                .aspectRatio(1f / 1f)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center
        )
    } else {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(width)
                .clip(CircleShape)
                .background(randomColor(name))
        ) {
            Text(
                text = name.firstOrNull()?.uppercase() ?: "?",
                color = Color.White,
                fontSize = (width.value / 2.5).sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

fun randomColor(name: String): Color {
    val colors = listOf(
        Color(0xFFEF5350), // red
        Color(0xFFAB47BC), // purple
        Color(0xFF42A5F5), // blue
        Color(0xFF26A69A), // teal
        Color(0xFF66BB6A), // green
        Color(0xFFFFA726), // orange
        Color(0xFF8D6E63)  // brown
    )
    val index = (name.hashCode().absoluteValue) % colors.size
    return colors[index]
}
