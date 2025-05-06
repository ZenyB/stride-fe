package com.trio.stride.ui.screens.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trio.stride.ui.components.activity.PreviewDetail
import com.trio.stride.ui.theme.StrideTheme

@Composable
fun ActivityScreen(viewModel: ActivityViewModel = hiltViewModel()) {

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(
                    bottom = WindowInsets.navigationBars.asPaddingValues()
                        .calculateBottomPadding()
                )
                .padding(bottom = 72.dp)
                .padding(top = padding.calculateTopPadding())
                .fillMaxSize(),
        ) {

            PreviewDetail()
        }
    }
}

@Composable
fun ProfileScreen() {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Profile Screen", style = StrideTheme.typography.headlineLarge)

        }
    }
}