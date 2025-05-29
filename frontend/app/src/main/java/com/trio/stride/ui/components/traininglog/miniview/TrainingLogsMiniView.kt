package com.trio.stride.ui.components.traininglog.miniview

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.trio.stride.navigation.Screen
import com.trio.stride.ui.components.traininglog.HorizontalTrainingLogSkeleton
import com.trio.stride.ui.components.traininglog.HorizontalTrainingLogValue
import com.trio.stride.ui.components.traininglog.HorizontalWeekTitles
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.getEndOfWeekInMillis
import com.trio.stride.ui.utils.getStartOfWeekInMillis
import java.time.LocalDate

@Composable
fun TrainingLogsMiniView(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: TrainingLogsMiniViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val todayIndex = LocalDate.now().dayOfWeek.value - 1
    val startDate = getStartOfWeekInMillis()
    val endDate = getEndOfWeekInMillis()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(StrideTheme.colorScheme.surface)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple()
            ) {
                navController.navigate(Screen.TrainingLogScreen.route)
            }, Alignment.Center
    ) {
        Column(modifier = modifier) {
            Text(
                "Training Log",
                style = StrideTheme.typography.titleLarge,
                color = StrideTheme.colorScheme.onSurface
            )
            Text(
                "See patterns in your training history.",
                style = StrideTheme.typography.labelLarge,
                color = StrideTheme.colors.gray
            )
            Spacer(Modifier.height(6.dp))
            if (state.trainingLogs.isEmpty()) {
                HorizontalTrainingLogSkeleton(
                    startDates = listOf(startDate),
                    endDates = listOf(endDate),
                    modifier = Modifier.padding(12.dp)
                )
            } else {
                HorizontalTrainingLogValue(
                    trainingLogsData = state.trainingLogs,
                    startDate = startDate,
                    endDate = endDate,
                    weekDataText = "",
                    onItemClick = {},
                    todayIndex = todayIndex,
                    modifier = Modifier.padding(12.dp)
                )
            }
            HorizontalWeekTitles()
            Spacer(Modifier.height(12.dp))
            Text(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 12.dp),
                text = "See more of your training",
                style = StrideTheme.typography.titleMedium.copy(color = StrideTheme.colorScheme.primary),
                textAlign = TextAlign.End
            )
        }
    }
}