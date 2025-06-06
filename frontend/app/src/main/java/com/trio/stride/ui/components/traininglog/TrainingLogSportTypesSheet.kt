package com.trio.stride.ui.components.traininglog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.graphics.toColorInt
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.trio.stride.R
import com.trio.stride.domain.model.Sport
import com.trio.stride.ui.theme.StrideTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingLogSportTypesSheet(
    onDismiss: () -> Unit,
    sports: List<Sport>,
    initialSelectedSports: List<Sport>,
    onChangeSelectedSports: (List<Sport>) -> Unit,
    modifier: Modifier = Modifier,
    visible: Boolean = false,
) {
    val selectedSports = remember { mutableStateOf(initialSelectedSports) }

    if (visible) {
        AnimatedVisibility(
            visible = true,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(durationMillis = 300)
            ),
            exit = slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = tween(durationMillis = 300)
            ),
            modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            ModalBottomSheet(
                containerColor = StrideTheme.colorScheme.surface,
                onDismissRequest = onDismiss,
                modifier = modifier
                    .zIndex(10000f)
                    .fillMaxWidth(),
            ) {
                Text(
                    "Sport",
                    modifier = Modifier.fillMaxWidth(),
                    style = StrideTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                HorizontalDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .padding(start = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "All sports",
                        style = StrideTheme.typography.titleMedium,
                        color = StrideTheme.colorScheme.onSurface
                    )
                    Checkbox(
                        checked = selectedSports.value.containsAll(sports),
                        onCheckedChange = { selected ->
                            if (selected) {
                                selectedSports.value = sports
                            } else {
                                selectedSports.value = emptyList()
                            }
                            onChangeSelectedSports(selectedSports.value)
                        }
                    )
                }
                HorizontalDivider()
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp)
                ) {
                    itemsIndexed(sports) { index, sport ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            AsyncImage(
                                modifier = Modifier.size(24.dp),
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(sport.image)
                                    .error(R.drawable.image_icon)
                                    .fallback(R.drawable.image_icon)
                                    .placeholder(R.drawable.image_icon)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Sport icon"
                            )
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(Color(sport.color.toColorInt()))
                            )
                            Text(
                                modifier = Modifier.weight(1f),
                                text = sport.name,
                                style = StrideTheme.typography.labelLarge,
                                color = StrideTheme.colorScheme.onSurface
                            )
                            Checkbox(
                                checked = selectedSports.value.contains(sport),
                                onCheckedChange = { selected ->
                                    if (selected) {
                                        val newSelectedSports = selectedSports.value.toMutableList()
                                        newSelectedSports.add(sport)
                                        selectedSports.value = newSelectedSports
                                    } else {
                                        val newSelectedSports = selectedSports.value.toMutableList()
                                        newSelectedSports.remove(sport)
                                        selectedSports.value = newSelectedSports
                                    }
                                    onChangeSelectedSports(selectedSports.value)
                                }
                            )
                        }
                        if (index != sports.size) {
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}