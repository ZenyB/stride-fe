package com.trio.stride.ui.screens.verifyOtp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.trio.stride.navigation.Screen
import com.trio.stride.ui.screens.signup.SignUpViewState


@Composable
fun VerifyOtpScreen(
    navController: NavController,
    userIdentity: String = "",
    viewModel: VerifyOtpViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val otpCode = viewModel.otpCode

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Enter OTP",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // OTP Input Field
        OutlinedTextField(
            value = otpCode,
            onValueChange = { viewModel.otpCode = it },
            label = { Text("OTP Code") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Verify OTP Button
        Button(
            onClick = {
                // Trigger OTP verification on button click, passing userIdentity
                viewModel.verifyOtp(otpCode, userIdentity)
            }, modifier = Modifier.fillMaxWidth()
        ) {
            Text("Verify OTP")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // OTP Verification State (Loading, Success, or Failure)
        when (uiState) {
            is VerifyOtpViewState.Idle -> {

            }

            is VerifyOtpViewState.Success -> {
                Text("OTP Verified Successfully", color = Color.Green)
            }

            is VerifyOtpViewState.Error -> {
                Text(
                    "Error: ${(uiState as VerifyOtpViewState.Error).message}",
                    color = Color.Red
                )
            }

            VerifyOtpViewState.Loading -> CircularProgressIndicator()

        }

    }

}

