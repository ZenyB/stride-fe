package com.trio.stride.ui.screens.verifyOtp

import android.util.Log
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.trio.stride.navigation.Screen
import com.trio.stride.ui.components.Loading
import com.trio.stride.ui.components.otp.OtpViewModel
import com.trio.stride.ui.components.otp.OtpAction
import com.trio.stride.ui.components.otp.OtpComponent
import com.trio.stride.ui.components.otp.OtpInputField
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.formatTime
import kotlinx.coroutines.delay


@Composable
fun VerifyOtpScreen(
    navController: NavController,
    userIdentity: String = "",
    viewModel: VerifyOtpViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val otpViewModel = viewModel<OtpViewModel>()
    val state by otpViewModel.state.collectAsStateWithLifecycle()
    val focusRequesters = remember {
        List(6) { FocusRequester() }
    }
    val keyboardManager = LocalSoftwareKeyboardController.current
    val buttonFocusRequester = remember { FocusRequester() }

    LaunchedEffect(true) {
        focusRequesters.getOrNull(0)?.requestFocus()
    }

    LaunchedEffect(state.focusedIndex) {
        val allNumbersEntered = state.code.none { it == null }

        state.focusedIndex?.let { index ->
            if (index != 0 || allNumbersEntered) {
                focusRequesters.getOrNull(index)?.requestFocus()
            }
        }
    }


    LaunchedEffect(state.code) {
        val allNumbersEntered = state.code.none { it == null }
        if (allNumbersEntered) {
            focusRequesters.forEach { it.freeFocus() }
            buttonFocusRequester.requestFocus()
            keyboardManager?.hide()
            viewModel.verifyOtp(state.code.joinToString(""), userIdentity)
        }
    }

    when (uiState) {
        is VerifyOtpViewState.Loading -> Loading()
        is VerifyOtpViewState.Success -> {
            navController.navigate(Screen.Auth.Login.route)
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
        ) {
            Spacer(Modifier.height(32.dp))

            Text(
                text = "We sent you a code",
                style = StrideTheme.typography.headlineLarge
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Please enter the 6-digit code we sent to your email",
                style = StrideTheme.typography.bodyMedium

            )

            OtpComponent(
                state = state,
                focusRequesters = focusRequesters,
                onAction = { action ->
                    when (action) {
                        is OtpAction.OnEnterNumber -> {
                            if (action.number != null) {
                                focusRequesters[action.index].freeFocus()
                            }
                        }

                        else -> Unit
                    }
                    otpViewModel.onAction(action)
                },
            )
            Button(
                onClick = {},
                modifier = Modifier
                    .focusRequester(buttonFocusRequester)
                    .focusable()
                    .size(0.dp)
            ) {}
            when (uiState) {
                is VerifyOtpViewState.Error -> {
                    Text(
                        text = (uiState as VerifyOtpViewState.Error).message,
                        color = StrideTheme.colorScheme.error
                    )
                }

                else -> {
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(
                    onClick = {
                        viewModel.sendOtp(userIdentity)
                    },
                    enabled = uiState !is VerifyOtpViewState.Countdown &&
                            uiState !is VerifyOtpViewState.Loading

                ) {
                    Text("Get a new code")
                }
                Spacer(modifier = Modifier.width(16.dp))

                when (uiState) {
                    is VerifyOtpViewState.Countdown -> {
                        val timeLeft = (uiState as VerifyOtpViewState.Countdown).second
                        Text(
                            "Resend in ${formatTime(timeLeft)}",
                            style = StrideTheme.typography.bodyMedium,
                            color = StrideTheme.colors.gray600
                        )
                    }

                    else -> {}
                }
            }
        }
    }
}
