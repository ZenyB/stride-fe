package com.trio.stride.ui.screens.signup


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.trio.stride.navigation.Screen

@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    // Observe the current state of the viewModel
    val uiState by viewModel.uiState.collectAsState()

    // Handle different UI states
    when (uiState) {
        is SignUpViewState.Idle -> {
            // Initial state: Show empty form
            SignUpForm(viewModel)
        }

        is SignUpViewState.Loading -> {
            // Show loading spinner
            CircularProgressIndicator()
        }

        is SignUpViewState.Success -> {
            val userIdentity = (uiState as SignUpViewState.Success).userIdentity

            LaunchedEffect(userIdentity) {
                navController.navigate(Screen.Auth.OTP.createRoute(userIdentity))
            }
        }

        is SignUpViewState.Error -> {
            // Show error message
            Text(
                text = (uiState as SignUpViewState.Error).message,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun SignUpForm(viewModel: SignUpViewModel) {
    val email = viewModel.email
    val password = viewModel.password

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { viewModel.email = it },
            label = { Text("Email") })
        OutlinedTextField(
            value = password,
            onValueChange = { viewModel.password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.signUp(email, password) }) {
            Text("Sign Up")
        }
    }
}
