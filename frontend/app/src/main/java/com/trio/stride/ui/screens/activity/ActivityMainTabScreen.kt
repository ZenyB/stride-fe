package com.trio.stride.ui.screens.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.trio.stride.ui.components.traininglog.miniview.TrainingLogsMiniView
import com.trio.stride.ui.screens.goal.view.GoalListPreview
import com.trio.stride.ui.screens.progress.ProgressOverview
import com.trio.stride.ui.theme.StrideTheme


@Composable
fun ActivityMainTabScreen(
    navController: NavController
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf("Progress", "Activities")

    Scaffold { padding ->
        Column(
            Modifier
                .padding(
                    bottom = WindowInsets.navigationBars.asPaddingValues()
                        .calculateBottomPadding()
                )
                .padding(bottom = 72.dp)
                .fillMaxSize()
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier
                    .background(StrideTheme.colorScheme.surface)
                    .padding(top = padding.calculateTopPadding())

            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = {
                            Text(
                                title,
                                color = if (selectedTabIndex == index)
                                    StrideTheme.colorScheme.onSurface else StrideTheme.colors.gray600,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp),

                    ) {
                    ProgressOverview(navController)

                    TrainingLogsMiniView(
                        navController,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                    )
                    GoalListPreview(navController)
                }

                1 -> ActivityScreen(
                    navController
                )
            }
        }
    }

}