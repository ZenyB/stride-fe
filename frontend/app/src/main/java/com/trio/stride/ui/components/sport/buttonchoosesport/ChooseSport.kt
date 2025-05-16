package com.trio.stride.ui.components.sport.buttonchoosesport

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.trio.stride.R
import com.trio.stride.domain.model.Sport
import com.trio.stride.ui.theme.StrideTheme

@Composable
fun ChooseSportIconButton(
    iconImage: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        colors = IconButtonDefaults.iconButtonColors().copy(
            containerColor = StrideTheme.colors.transparent,
            contentColor = StrideTheme.colorScheme.onSurface
        )
    ) {
        Icon(
            painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(iconImage)
                    .error(R.drawable.image_icon)
                    .fallback(R.drawable.image_icon)
                    .placeholder(R.drawable.image_icon)
                    .crossfade(true)
                    .build()
            ),
            contentDescription = "Sport Icon",
            modifier = iconModifier,
            tint = StrideTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ChooseSportInSearch(
    iconImage: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = StrideTheme.colors.transparent,
            contentColor = StrideTheme.colorScheme.primary
        ),
        contentPadding = PaddingValues(
            start = 10.dp,
            top = 8.dp,
            end = 10.dp,
            bottom = 8.dp
        ),
        shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(32.dp),
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(iconImage)
                        .error(R.drawable.image_icon)
                        .fallback(R.drawable.image_icon)
                        .placeholder(R.drawable.image_icon)
                        .crossfade(true)
                        .build()
                ),
                contentDescription = "Sport Icon",
                tint = StrideTheme.colorScheme.primary
            )
            Spacer(Modifier.width(4.dp))
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(R.drawable.park_down_icon),
                contentDescription = "Park down icon",
                tint = StrideTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ChooseSportInActivity(
    sport: Sport,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(
                BorderStroke(1.dp, StrideTheme.colors.grayBorder),
                RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true)
            ) {
                onClick()
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.padding(start = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(sport.image)
                        .error(R.drawable.image_icon)
                        .fallback(R.drawable.image_icon)
                        .placeholder(R.drawable.image_icon)
                        .crossfade(true)
                        .build()
                ),
                contentDescription = "Sport Image",
                tint = StrideTheme.colorScheme.onBackground
            )
            Spacer(Modifier.width(8.dp))
            Text(sport.name, style = textStyle ?: StrideTheme.typography.labelMedium)
        }
        IconButton(
            modifier = Modifier.padding(end = 8.dp),
            onClick = onClick,
        ) {
            Icon(
                painter = painterResource(R.drawable.park_down_icon),
                contentDescription = "Show sport menu",
                tint = StrideTheme.colorScheme.onBackground,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}