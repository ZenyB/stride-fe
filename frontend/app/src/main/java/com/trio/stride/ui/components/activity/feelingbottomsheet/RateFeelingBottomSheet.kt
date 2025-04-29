package com.trio.stride.ui.components.activity.feelingbottomsheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trio.stride.ui.theme.StrideTheme
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RateFeelingBottomSheet(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    state: RateFeelingBottomSheetState = hiltViewModel()
) {
    val showBottomSheet by state.showBottomSheet.collectAsStateWithLifecycle()
    val feelingRate by state.feelingRate.collectAsStateWithLifecycle()
    val feelingStatusText by state.feelingStatusText.collectAsStateWithLifecycle()

    AnimatedVisibility(
        visible = showBottomSheet,
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
            onDismissRequest = { state.hide() },
            modifier = modifier
                .zIndex(10000f)
                .background(StrideTheme.colorScheme.surfaceContainerLowest)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Perceived Exertion",
                        style = StrideTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = feelingStatusText,
                            style = StrideTheme.typography.titleMedium
                        )

                        TextButton(
                            onClick = { state.updateFeelingRate(0) }
                        ) {
                            Text(
                                text = "Clear Entry",
                                style = StrideTheme.typography.labelSmall.copy(
                                    color = StrideTheme.colorScheme.primary
                                )
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Value: ${value}")

                        Slider(
                            value = value.toFloat(),
                            onValueChange = { newValue ->
                                state.updateFeelingRate(newValue.roundToInt())
                                onValueChange(newValue.roundToInt())
                            },
                            valueRange = 0f..10f,
                            steps = 9,
                            colors = SliderDefaults.colors().copy(
                                inactiveTrackColor = StrideTheme.colorScheme.background,
                                inactiveTickColor = StrideTheme.colorScheme.primary,
                                activeTickColor = StrideTheme.colorScheme.primary,
                            )
                        )
                    }
                }
            }
        }
    }
}