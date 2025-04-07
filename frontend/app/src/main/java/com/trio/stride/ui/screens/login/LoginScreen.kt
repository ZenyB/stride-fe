package com.trio.stride.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.trio.stride.ui.components.Loading
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.R
import com.trio.stride.ui.theme.StrideColor

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onUnAuthorized: () -> Unit,
    onSignUp: () -> Unit,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val state by loginViewModel.uiState.collectAsStateWithLifecycle()
    val (focusRequesterPassword) = remember { FocusRequester.createRefs() }
    val keyboardController = LocalSoftwareKeyboardController.current

    when (state.state) {
        LoginViewModel.LoginState.LOADING -> Loading()
        LoginViewModel.LoginState.SUCCESS -> onLoginSuccess()
        LoginViewModel.LoginState.UNAUTHORIZED -> onUnAuthorized()
        else -> {}
    }

    Scaffold() { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                modifier = Modifier.height(80.dp).align(Alignment.CenterHorizontally),
                painter = painterResource(R.drawable.app_name),
                contentDescription = "App name",
                contentScale = ContentScale.FillHeight)
            Spacer(Modifier.height(16.dp))

            Text("Log In to Stride", style = StrideTheme.typography.headlineLarge)
            Spacer(Modifier.height(32.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
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
                isError = state.message.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        loginViewModel.login(email, password)
                    }
                ),
                isError = state.message.isNotBlank(),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequesterPassword)
            )

            if (state.message.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Error: ${state.message}", color = StrideTheme.colorScheme.error)

            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {loginViewModel.login(email, password)},
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login", style = StrideTheme.typography.titleMedium)
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
                modifier = Modifier.height(42.dp).fillMaxWidth()) {
                Row(Modifier.fillMaxWidth(), Arrangement.Center) {
                    Image(painter = painterResource(R.drawable.google), contentDescription = "Google Icon")
                    Spacer(Modifier.width(12.dp))
                    Text("Log In with Google", style = StrideTheme.typography.titleMedium)
                }
            }

            Spacer(Modifier.height(12.dp))
            TextButton(onClick = { onSignUp() }, modifier = Modifier.align(Alignment.End), shape = RoundedCornerShape(8.dp),) {
                Row(Modifier, Arrangement.Center) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Arrow Icon")
                    Spacer(Modifier.width(12.dp))
                    Text("Sign Up", style = StrideTheme.typography.titleMedium)
                }
            }
        }
    }
}