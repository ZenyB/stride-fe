package com.trio.stride.ui.screens.goal.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.trio.stride.navigation.Screen
import com.trio.stride.ui.components.goal.GoalItemHomePreview
import com.trio.stride.ui.theme.StrideTheme
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

@Composable
fun GoalListHomePreview(
    navController: NavController, viewModel: GoalListViewModel = hiltViewModel()
) {
    val items = viewModel.items
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.View)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate(Screen.GoalListScreen.route)
            }
            .background(StrideTheme.colorScheme.surface)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        if (uiState is UserGoalState.Idle) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Goals", style = StrideTheme.typography.titleMedium)
                TextButton(onClick = {
                    navController.navigate(Screen.CreateGoalScreen.route)
                }) {
                    Text(
                        "Add Goal",
                        style = StrideTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                items.take(3).forEach { goal ->
                    GoalItemHomePreview(goal)
                }
            }
        }


        if (uiState is UserGoalState.Loading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .shimmer(shimmer)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray)
                )
            }
        }

        if (uiState is UserGoalState.Error) {
            Box(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    (uiState as UserGoalState.Error).message,
                    style = StrideTheme.typography.titleMedium,
                    color = StrideTheme.colors.red700
                )
            }
        }
    }
}