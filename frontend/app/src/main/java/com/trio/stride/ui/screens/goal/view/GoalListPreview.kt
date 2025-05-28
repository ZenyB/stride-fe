package com.trio.stride.ui.screens.goal.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.trio.stride.navigation.Screen
import com.trio.stride.ui.components.LoadingSmall
import com.trio.stride.ui.components.goal.GoalItemView
import com.trio.stride.ui.theme.StrideTheme


@Composable
fun GoalListPreview(
    navController: NavController, viewModel: GoalListViewModel = hiltViewModel()
) {
    val items = viewModel.items
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(StrideTheme.colorScheme.surface)
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp, bottom = 16.dp),
    ) {
        if (uiState is UserGoalState.Idle) {
            Text("Goals", style = StrideTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            items.take(3).forEach { goal ->
                GoalItemView(
                    goal, preview = true,
                    onActionClick = {
                    }
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = {
                    navController.navigate(Screen.GoalListScreen.route)
                }) {
                    Text(
                        "Add Goal",
                        style = StrideTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
                TextButton(onClick = {
                    navController.navigate(Screen.GoalListScreen.route)
                }) {
                    Text(
                        "See All Your Goals",
                        style = StrideTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
        if (uiState is UserGoalState.Loading) {
            Box(
                modifier = Modifier
                    .padding(40.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                LoadingSmall()
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


