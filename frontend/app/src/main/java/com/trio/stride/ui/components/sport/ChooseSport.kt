package com.trio.stride.ui.components.sport

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.trio.stride.R
import com.trio.stride.domain.model.Sport
import com.trio.stride.ui.components.sport.bottomsheet.SportBottomSheetState
import com.trio.stride.ui.theme.StrideTheme

@Composable
fun ChooseSportIconButton(
    iconImage: String,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    state: SportBottomSheetState = hiltViewModel(),
) {
    IconButton(
        onClick = { state.show() },
        modifier = modifier
    ) {
        Icon(
            painter = rememberAsyncImagePainter(model = iconImage),
            contentDescription = "Sport Icon",
            modifier = iconModifier.size(32.dp),
            tint = StrideTheme.colorScheme.primary
        )
    }
}

@Composable
fun ChooseSportInSearch(
    iconImage: String,
    modifier: Modifier = Modifier,
    state: SportBottomSheetState = hiltViewModel(),
) {
    Button(
        modifier = modifier,
        onClick = { state.show() },
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = StrideTheme.colors.transparent,
            contentColor = StrideTheme.colorScheme.primary
        )
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(32.dp),
                painter = rememberAsyncImagePainter(model = iconImage),
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
    modifier: Modifier = Modifier,
    state: SportBottomSheetState = hiltViewModel(),
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
                state.show()
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
                painter = rememberAsyncImagePainter(sport.image),
                contentDescription = "Sport Image",
                tint = StrideTheme.colorScheme.onBackground
            )
            Spacer(Modifier.width(8.dp))
            Text(sport.name, style = StrideTheme.typography.labelMedium)
        }
        IconButton(
            modifier = Modifier.padding(end = 8.dp),
            onClick = { state.show() }
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