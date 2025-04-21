package com.trio.stride.ui.screens.activity

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trio.stride.ui.screens.heartrate.HeartRateViewModel
import com.trio.stride.ui.screens.signup.SignUpViewModel
import com.trio.stride.ui.theme.StrideTheme

@Composable
fun ActivityScreen(viewModel: ActivityViewModel = hiltViewModel()) {

    val heartRate by viewModel.heartRate.collectAsState()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Activity Screen", style = StrideTheme.typography.headlineLarge)
            Column {
                Text(
                    text = "Heart rate: ${heartRate}",
                    style = StrideTheme.typography.labelMedium,
                    color = StrideTheme.colors.gray600
                )
            }
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

@Composable
fun RecordScreen() {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Record Screen", style = StrideTheme.typography.headlineLarge)

        }
    }
}