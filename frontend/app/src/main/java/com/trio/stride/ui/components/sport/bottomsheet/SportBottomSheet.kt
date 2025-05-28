package com.trio.stride.ui.components.sport.bottomsheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.trio.stride.R
import com.trio.stride.domain.model.Category
import com.trio.stride.domain.model.Sport
import com.trio.stride.ui.theme.StrideTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SportBottomSheetWithCategory(
    categories: List<Category>,
    sportsByCategory: Map<Category, List<Sport>>,
    selectedSport: Sport?,
    visible: Boolean,
    dismissAction: () -> Unit,
    onItemClick: (Sport) -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(durationMillis = 300)
        ),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(durationMillis = 300)
        )
    ) {
        ModalBottomSheet(
            containerColor = StrideTheme.colorScheme.surface,
            onDismissRequest = { dismissAction() },
            modifier = modifier.zIndex(10000f)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                item {
                    Text(
                        "Choose a Sport",
                        style = StrideTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    HorizontalDivider()
                }
                items(categories) { category ->
                    val sports = sportsByCategory[category] ?: emptyList()
                    Column(
                        modifier = Modifier
                    ) {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = category.name,
                            style = StrideTheme.typography.titleMedium
                        )

                        sports.forEach { sport ->
                            val contentColor = if (sport.id == selectedSport?.id)
                                StrideTheme.colorScheme.primary
                            else
                                StrideTheme.colorScheme.onBackground
                            Spacer(Modifier.height(16.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = ripple()
                                    ) {
                                        onItemClick(sport)
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = rememberAsyncImagePainter(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(sport.image)
                                                .error(R.drawable.image_icon)
                                                .fallback(R.drawable.image_icon)
                                                .placeholder(R.drawable.image_icon)
                                                .crossfade(true)
                                                .build(),
                                        ),
                                        modifier = Modifier.size(32.dp),
                                        contentDescription = "Sport image",
                                        tint = contentColor
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        text = sport.name,
                                        style = StrideTheme.typography.labelLarge.copy(
                                            fontSize = 16.sp
                                        ),
                                        color = contentColor,
                                    )
                                }
                                if (sport.id == selectedSport?.id) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        modifier = Modifier.size(32.dp),
                                        contentDescription = "Is selected sport",
                                        tint = StrideTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SportMapBottomSheet(
    modifier: Modifier = Modifier,
    sports: List<Sport>,
    selectedSport: Sport?,
    visible: Boolean,
    dismissAction: () -> Unit,
    onItemClick: (Sport) -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(durationMillis = 300)
        ),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(durationMillis = 300)
        )
    ) {
        ModalBottomSheet(
            containerColor = StrideTheme.colorScheme.surface,
            onDismissRequest = { dismissAction() },
            modifier = modifier.zIndex(10000f)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                item {
                    Text(
                        "Choose a Sport",
                        style = StrideTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    HorizontalDivider()
                }
                items(sports) { sport ->
                    val contentColor = if (sport.id == selectedSport?.id)
                        StrideTheme.colorScheme.primary
                    else
                        StrideTheme.colorScheme.onBackground
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple()
                            ) {
                                onItemClick(sport)
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = rememberAsyncImagePainter(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(sport.image)
                                        .error(R.drawable.image_icon)
                                        .fallback(R.drawable.image_icon)
                                        .placeholder(R.drawable.image_icon)
                                        .crossfade(true)
                                        .build()
                                ),
                                modifier = Modifier.size(32.dp),
                                contentDescription = "Sport image",
                                tint = contentColor
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = sport.name,
                                style = StrideTheme.typography.labelLarge.copy(fontSize = 16.sp),
                                color = contentColor,
                            )
                        }
                        if (sport.id == selectedSport?.id) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                modifier = Modifier.size(32.dp),
                                contentDescription = "Is selected sport",
                                tint = StrideTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}