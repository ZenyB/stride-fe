package com.trio.stride.ui.screens.signup


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.trio.stride.R
import com.trio.stride.navigation.Screen
import com.trio.stride.ui.components.Loading
import com.trio.stride.ui.theme.StrideTheme

@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val (focusRequesterPassword) = remember { FocusRequester.createRefs() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val email = viewModel.email
    val password = viewModel.password

    when (uiState) {
        is SignUpViewState.Loading -> Loading()

        is SignUpViewState.Success -> {
            val userIdentity = (uiState as SignUpViewState.Success).userIdentity

            LaunchedEffect(userIdentity) {
                navController.navigate(Screen.Auth.OTP.createRoute(userIdentity))
            }
        }
        else -> {
        }
    }

    Scaffold() { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(16.dp))

            Text("Create an Account", style = StrideTheme.typography.headlineLarge)
            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusRequesterPassword.requestFocus()
                    }
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        viewModel.signUp(email, password)
                    }
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequesterPassword)
            )

            if (uiState is SignUpViewState.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (uiState as SignUpViewState.Error).message,
                    color = StrideTheme.colorScheme.error
                )

            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.signUp(email, password) },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign Up", style = StrideTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(thickness = 2.dp)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {},
                colors = ButtonDefaults.outlinedButtonColors().copy(
                    containerColor = StrideTheme.colors.transparent,
                    contentColor = StrideTheme.colors.gray600
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .height(42.dp)
                    .fillMaxWidth()
            ) {
                Row(Modifier.fillMaxWidth(), Arrangement.Center) {
                    Image(
                        painter = painterResource(R.drawable.google),
                        contentDescription = "Google Icon"
                    )
                    Spacer(Modifier.width(12.dp))
                    Text("Continue with Google", style = StrideTheme.typography.titleMedium)
                }
            }

            Spacer(Modifier.height(12.dp))
            TextButton(
                onClick = { navController.navigate(Screen.Auth.Login.route) },
                modifier = Modifier.align(Alignment.End),
                shape = RoundedCornerShape(8.dp),
            ) {
                Row(Modifier, Arrangement.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Arrow Icon"
                    )
                    Spacer(Modifier.width(12.dp))
                    Text("Go to Login", style = StrideTheme.typography.titleMedium)
                }
            }
        }
    }
}

