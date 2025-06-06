package com.trio.stride.ui.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trio.stride.R
import com.trio.stride.ui.components.Loading
import com.trio.stride.ui.components.textfield.CalendarTextField
import com.trio.stride.ui.components.textfield.CustomOutlinedTextField
import com.trio.stride.ui.components.textfield.GenderTextField
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.toBoolGender
import com.trio.stride.ui.utils.toGender
import java.time.LocalDate

@Composable
fun OnboardingScreen(
    navigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val focusRequesterWeight = remember { FocusRequester() }

    when (val viewState = state.viewState) {
        is OnboardingViewModel.OnboardingViewState.Success -> {
            navigateToHome()
        }

        is OnboardingViewModel.OnboardingViewState.Welcome -> {}

        is OnboardingViewModel.OnboardingViewState.Info -> {
            Box {
                Scaffold(
                    containerColor = StrideTheme.colorScheme.surface
                ) { padding ->
                    Box(modifier = modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp)
                                .align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Welcome to\nStride",
                                style = StrideTheme.typography.displayLarge,
                                color = StrideTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(16.dp))
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                OutlinedTextField(
                                    value = viewState.name,
                                    textStyle = StrideTheme.typography.labelLarge,
                                    onValueChange = { viewModel.updateName(it) },
                                    label = { Text("Your name") },
                                    placeholder = {
                                        Text(
                                            "Your name",
                                            style = StrideTheme.typography.labelLarge
                                        )
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    isError = viewState.isError,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                viewState.errorMessage?.let {
                                    Text(
                                        "Name can not be blank",
                                        style = StrideTheme.typography.labelMedium.copy(color = StrideTheme.colorScheme.error)
                                    )
                                }

                                CalendarTextField(
                                    value = viewState.dob,
                                    initialDate = LocalDate.now().minusYears(20)
                                ) {
                                    viewModel.updateDob(it)
                                }

                                GenderTextField(
                                    value = viewState.male.toGender(),
                                ) {
                                    viewModel.updateGender(it.toBoolGender())
                                }

                                CustomOutlinedTextField(
                                    value = viewState.height.toString(),
                                    suffix = {
                                        Text(text = "cm", style = StrideTheme.typography.labelLarge)
                                    },
                                    onValueChange = { newValue ->
                                        if (newValue.all { it.isDigit() }) {
                                            if (newValue.isBlank())
                                                viewModel.updateHeight(0)
                                            else viewModel.updateHeight(newValue.toInt())
                                        }
                                    },
                                    label = { Text("Height") },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Next
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onNext = {
                                            focusRequesterWeight.requestFocus()
                                        }
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                CustomOutlinedTextField(
                                    value = viewState.weight.toString(),
                                    suffix = {
                                        Text(text = "kg", style = StrideTheme.typography.labelLarge)
                                    },
                                    onValueChange = { newValue ->
                                        if (newValue.all { it.isDigit() }) {
                                            if (newValue.isBlank())
                                                viewModel.updateWeight(0)
                                            else viewModel.updateWeight(newValue.toInt())
                                        }
                                    },
                                    label = { Text("Weight") },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            viewModel.success()
                                        }
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .focusRequester(focusRequesterWeight)
                                )
                            }
                            Spacer(Modifier.height(32.dp))
                            Button(
                                onClick = { viewModel.updateUser() }
                            ) {
                                Text("Continue", style = StrideTheme.typography.labelLarge)
                            }
                        }
                        Icon(
                            modifier = Modifier
                                .size(120.dp)
                                .align(Alignment.BottomCenter),
                            painter = painterResource(R.drawable.app_name),
                            contentDescription = "App name icon",
                            tint = StrideTheme.colorScheme.primary
                        )
                    }
                }

                if (viewState.isLoading) {
                    Loading()
                }
            }
        }
    }
}