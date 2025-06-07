package com.trio.stride.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trio.stride.R
import com.trio.stride.ui.components.Avatar
import com.trio.stride.ui.components.CustomLeftTopAppBar
import com.trio.stride.ui.components.Loading
import com.trio.stride.ui.components.dialog.StrideDialog
import com.trio.stride.ui.components.librarypicker.rememberImagePickerLauncher
import com.trio.stride.ui.components.textfield.BirthDayTextField
import com.trio.stride.ui.components.textfield.CustomOutlinedTextField
import com.trio.stride.ui.components.textfield.GenderTextField
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.advancedShadow
import com.trio.stride.ui.utils.toDate
import com.trio.stride.ui.utils.toGender

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    handleBottomBarVisibility: (Boolean) -> Unit,
    onLogOutSuccess: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()
    val cityFocusRequester = remember { FocusRequester() }
    val heightFocusRequester = remember { FocusRequester() }
    val weightFocusRequester = remember { FocusRequester() }
    val shoesFocusRequester = remember { FocusRequester() }
    val bagFocusRequester = remember { FocusRequester() }
    val selectedAvaUri = remember { mutableStateOf<Uri?>(null) }
    var shouldLaunchImagePicker by remember { mutableStateOf(true) }
    val interactionSource = remember { null }

    val imageUri = remember(selectedAvaUri.value, state.userInfo.ava) {
        selectedAvaUri.value ?: state.userInfo.ava
    }

    val openImagePicker = rememberImagePickerLauncher { selectedUri ->
        shouldLaunchImagePicker = true
        selectedAvaUri.value = selectedUri
    }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val textFieldColor = TextFieldDefaults.colors(
        focusedIndicatorColor = if (!state.isEditProfile) StrideTheme.colors.gray else StrideTheme.colorScheme.primary,
        unfocusedIndicatorColor = StrideTheme.colors.gray,
        unfocusedContainerColor = StrideTheme.colors.transparent,
        focusedContainerColor = StrideTheme.colors.transparent,
        disabledIndicatorColor = StrideTheme.colors.gray,
        disabledContainerColor = StrideTheme.colors.transparent,
        disabledTextColor = StrideTheme.colorScheme.onSurface,
        disabledLabelColor = StrideTheme.colorScheme.onSurface,
        disabledSuffixColor = StrideTheme.colorScheme.onSurface,
    )

    if (state.logoutSuccess) {
        onLogOutSuccess()
    }

    BackHandler {
        if (state.isEditProfile) {
            viewModel.changeToDefault()
        } else {
            onBack()
        }
    }

    LaunchedEffect(state.isEditProfile) {
        selectedAvaUri.value = null
        handleBottomBarVisibility(!state.isEditProfile)
    }

    StrideDialog(
        visible = state.isError,
        title = "Something Error",
        description = "Stride can not update your profile now\nPlease try again after few minutes.",
        dismiss = { viewModel.clearError() },
        neutralText = "OK",
        neutral = { viewModel.clearError() }
    )

    StrideDialog(
        visible = state.isUpdateSuccess && state.isEditProfile,
        title = "Profile updated successfully",
        description = "Keep living healthy — small steps every day make a big difference.",
        dismiss = { },
        doneText = "Done",
        done = { viewModel.changeToDefault() }
    )

    StrideDialog(
        visible = state.isNotSync,
        title = "Sync Error",
        description = "Update successful but data may not be synchronized",
        dismiss = { viewModel.ignoreIsNotSync() },
        neutralText = "OK",
        neutral = { viewModel.ignoreIsNotSync() }
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(StrideTheme.colorScheme.surface)
            .padding(WindowInsets.ime.union(WindowInsets.navigationBars).asPaddingValues())
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                focusManager.clearFocus()
            }) {
        Scaffold(
            containerColor = StrideTheme.colorScheme.surface,
            topBar = {
                CustomLeftTopAppBar(
                    title = if (state.isEditProfile) "Edit Profile" else "Profile",
                    navigationIcon = {
                        if (state.isEditProfile) {
                            IconButton(
                                onClick = { viewModel.changeToDefault() }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.park_down_icon),
                                    contentDescription = "Hide",
                                    tint = StrideTheme.colorScheme.onBackground,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .rotate(90f)
                                )
                            }
                        }
                    },
                    actions = {
                        if (state.isEditProfile) {
                            TextButton(
                                onClick = {
                                    viewModel.updateProfile(context, selectedAvaUri.value)
                                },
                                enabled = !state.isLoading || !state.isError
                            ) {
                                Text(text = "DONE", style = StrideTheme.typography.titleMedium)
                            }
                        } else {
                            TextButton(
                                onClick = {
                                    viewModel.changeToEdit()
                                },
                            ) {
                                Text(text = "EDIT", style = StrideTheme.typography.titleMedium)
                            }
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = padding.calculateTopPadding() + 32.dp)
                    .padding(bottom = (if (state.isEditProfile) 0.dp else 72.dp) + 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .advancedShadow(
                            cornersRadius = 1000.dp
                        )
                        .clip(CircleShape)
                        .background(StrideTheme.colorScheme.inversePrimary, CircleShape)
                        .size(68.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true)
                        ) {
                            if (shouldLaunchImagePicker && state.isEditProfile) {
                                openImagePicker()
                                shouldLaunchImagePicker = false
                            }
                        }
                ) {
                    Avatar(
                        ava = imageUri,
                        name = state.userInfo.name,
                        width = 68.dp
                    )
                }

                ProfileColumnContainer("Full Name") {
                    CustomOutlinedTextField(
                        value = state.userInfo.name,
                        onValueChange = { viewModel.updateName(it) },
                        readOnly = !state.isEditProfile,
                        enabled = state.isEditProfile,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                cityFocusRequester.requestFocus()
                            }
                        ),
                        colors = textFieldColor,
                        interactionSource = interactionSource,
                        isError = state.errorFields[ProfileViewModel.ErrorField.NAME] ?: false,
                        errorMessage = "Name is blank"
                    )
                }

                ProfileColumnContainer("City") {
                    CustomOutlinedTextField(
                        modifier = Modifier.focusRequester(cityFocusRequester),
                        value = state.userInfo.city,
                        onValueChange = { viewModel.updateCity(it) },
                        readOnly = !state.isEditProfile,
                        enabled = state.isEditProfile,
                        singleLine = true,
                        colors = textFieldColor,
                        interactionSource = interactionSource,
                    )
                }

                ProfileColumnContainer("Birth Day") {
                    BirthDayTextField(
                        value = state.userInfo.dob,
                        initialDate = state.userInfo.dob.toDate(),
                        label = null,
                        onDateChange = { viewModel.updateDob(it) },
                        colors = textFieldColor,
                        readOnly = !state.isEditProfile,
                        enable = state.isEditProfile,
                        isError = state.errorFields[ProfileViewModel.ErrorField.DOB]
                            ?: false,
                        errorMessage = "Your birthday is not valid"
                    )
                }

                ProfileColumnContainer("Gender") {
                    GenderTextField(
                        value = state.userInfo.male.toGender(),
                        label = null,
                        colors = textFieldColor,
                        readOnly = !state.isEditProfile,
                        enable = state.isEditProfile,
                        onGenderChange = { viewModel.updateGender(it) }
                    )
                }

                ProfileColumnContainer("Max Heart Rate") {
                    CustomOutlinedTextField(
                        value = state.userInfo.maxHeartRate.toString(),
                        onValueChange = { newValue ->
                            if (newValue.all { it.isDigit() }) {
                                if (newValue.isBlank())
                                    viewModel.updateMaxHeartRate(0)
                                else viewModel.updateMaxHeartRate(newValue.toInt())
                            }
                        },
                        suffix = {
                            Text("bpm", style = StrideTheme.typography.labelLarge)
                        },
                        readOnly = !state.isEditProfile,
                        enabled = state.isEditProfile,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { heightFocusRequester.requestFocus() }
                        ),
                        colors = textFieldColor,
                        interactionSource = interactionSource,
                        isError = state.errorFields[ProfileViewModel.ErrorField.HEART_RATE]
                            ?: false,
                        errorMessage = "Heart rate must be between 20 and 260"
                    )
                }

                ProfileColumnContainer("Height") {
                    CustomOutlinedTextField(
                        modifier = Modifier.focusRequester(heightFocusRequester),
                        value = state.userInfo.height.toString(),
                        onValueChange = { newValue ->
                            if (newValue.all { it.isDigit() }) {
                                if (newValue.isBlank())
                                    viewModel.updateHeight(0)
                                else viewModel.updateHeight(newValue.toInt())
                            }
                        },
                        suffix = {
                            Text("cm", style = StrideTheme.typography.labelLarge)
                        },
                        readOnly = !state.isEditProfile,
                        enabled = state.isEditProfile,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { weightFocusRequester.requestFocus() }
                        ),
                        colors = textFieldColor,
                        interactionSource = interactionSource,
                        isError = state.errorFields[ProfileViewModel.ErrorField.HEIGHT]
                            ?: false,
                        errorMessage = "Height is invalid"
                    )
                }

                ProfileColumnContainer("Weight") {
                    CustomOutlinedTextField(
                        modifier = Modifier.focusRequester(weightFocusRequester),
                        value = state.userInfo.weight.toString(),
                        onValueChange = { newValue ->
                            if (newValue.all { it.isDigit() }) {
                                if (newValue.isBlank())
                                    viewModel.updateWeight(0)
                                else viewModel.updateWeight(newValue.toInt())
                            }
                        },
                        suffix = {
                            Text("kg", style = StrideTheme.typography.labelLarge)
                        },
                        readOnly = !state.isEditProfile,
                        enabled = state.isEditProfile,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { shoesFocusRequester.requestFocus() }
                        ),
                        colors = textFieldColor,
                        interactionSource = interactionSource,
                        isError = state.errorFields[ProfileViewModel.ErrorField.WEIGHT]
                            ?: false,
                        errorMessage = "Weight is invalid"
                    )
                }

                ProfileColumnContainer("Equipments Weight") {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CustomOutlinedTextField(
                            containerModifier = Modifier.weight(1f),
                            modifier = Modifier
                                .focusRequester(shoesFocusRequester),
                            value = state.userInfo.equipmentsWeight.shoes.toString(),
                            onValueChange = { newValue ->
                                if (newValue.all { it.isDigit() }) {
                                    if (newValue.isBlank())
                                        viewModel.updateShoesWeight(0)
                                    else viewModel.updateShoesWeight(newValue.toInt())
                                }
                            },
                            label = {
                                Text("Shoes Weight", style = StrideTheme.typography.labelLarge)
                            },
                            suffix = {
                                Text("kg", style = StrideTheme.typography.labelLarge)
                            },
                            readOnly = !state.isEditProfile,
                            enabled = state.isEditProfile,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { bagFocusRequester.requestFocus() }
                            ),
                            colors = textFieldColor,
                            interactionSource = interactionSource,
                            isError = state.errorFields[ProfileViewModel.ErrorField.SHOES]
                                ?: false,
                            errorMessage = "Shoes weight is invalid"
                        )
                        CustomOutlinedTextField(
                            containerModifier = Modifier
                                .weight(1f),
                            modifier = Modifier
                                .focusRequester(bagFocusRequester),
                            value = state.userInfo.equipmentsWeight.bag.toString(),
                            onValueChange = { newValue ->
                                if (newValue.all { it.isDigit() }) {
                                    if (newValue.isBlank())
                                        viewModel.updateBagWeight(0)
                                    else viewModel.updateBagWeight(newValue.toInt())
                                }
                            },
                            label = {
                                Text("Bag Weight", style = StrideTheme.typography.labelLarge)
                            },
                            suffix = {
                                Text("kg", style = StrideTheme.typography.labelLarge)
                            },
                            readOnly = !state.isEditProfile,
                            enabled = state.isEditProfile,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    viewModel.updateProfile(
                                        context,
                                        selectedAvaUri.value
                                    )
                                }
                            ),
                            colors = textFieldColor,
                            interactionSource = interactionSource,
                            isError = state.errorFields[ProfileViewModel.ErrorField.BAG]
                                ?: false,
                            errorMessage = "Bag weight is invalid"
                        )
                    }
                }

                Button(
                    onClick = { viewModel.logout(context) }
                ) {
                    Text("Sign Out", style = StrideTheme.typography.titleMedium)
                }
            }
        }

        if (state.isLoading || state.isUploadImage || state.loggingOut) {
            Loading()
        }
    }
}

@Composable
private fun ProfileColumnContainer(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(title, style = StrideTheme.typography.titleMedium)
        content()
    }
}