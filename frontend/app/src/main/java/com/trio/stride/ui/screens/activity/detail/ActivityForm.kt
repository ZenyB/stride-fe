package com.trio.stride.ui.screens.activity.detail

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.trio.stride.R
import com.trio.stride.data.mapper.toRpeString
import com.trio.stride.domain.model.SportMapType
import com.trio.stride.ui.components.CustomLeftTopAppBar
import com.trio.stride.ui.components.Loading
import com.trio.stride.ui.components.activity.feelingbottomsheet.RateFeelingBottomSheet
import com.trio.stride.ui.components.activity.feelingbottomsheet.RateFeelingBottomSheetState
import com.trio.stride.ui.components.dialog.StrideDialog
import com.trio.stride.ui.components.librarypicker.ImagePickerView
import com.trio.stride.ui.components.sport.bottomsheet.SportBottomSheetWithCategory
import com.trio.stride.ui.components.sport.buttonchoosesport.ChooseSportInActivity
import com.trio.stride.ui.components.textfield.CustomOutlinedTextField
import com.trio.stride.ui.theme.StrideTheme

@Composable
fun ActivityFormView(
    title: String,
    primaryActionLabel: String,
    dismissAction: () -> Unit,
    isSaving: Boolean,
    mode: ActivityFormMode,
    modifier: Modifier = Modifier,
    viewModel: ActivityFormViewModel = hiltViewModel(),
    feelingBottomSheetState: RateFeelingBottomSheetState = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val sportsByCategory by viewModel.sportsByCategory.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current
    val localConfig = LocalConfiguration.current
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val previewImage = remember { mutableStateListOf<Uri>() }
    val expandImageOptionMenu = remember { mutableStateOf(false) }
    val selectedPreviewImageIndex = remember { mutableStateOf<Int?>(null) }
    var showSportBottomSheet by remember { mutableStateOf(false) }

    val sport = when (mode) {
        is ActivityFormMode.Create -> mode.sportFromRecord!!
        is ActivityFormMode.Update -> mode.activity.sport
    }
    var selectedSport by remember { mutableStateOf(sport) }
    var isShowDiscardDialog by remember { mutableStateOf(false) }
    val discardDialogTitle = when (mode) {
        is ActivityFormMode.Create -> "Discard Activity"
        is ActivityFormMode.Update -> "Discard Unsaved Changes"
    }
    val discardDialogSubTitle = when (mode) {
        is ActivityFormMode.Create -> "Are you sure to discard unsaved activity?"
        is ActivityFormMode.Update -> "Are you sure to discard unsaved changes?"
    }

    LaunchedEffect(Unit) {
        viewModel.initial(mode)
    }

    StrideDialog(
        visible = isShowDiscardDialog,
        title = discardDialogTitle,
        subtitle = discardDialogSubTitle,
        dismiss = { isShowDiscardDialog = false },
        dismissText = "Cancel",
        destructiveText = "Discard",
        destructive = {
            when (mode) {
                is ActivityFormMode.Create -> {
                    mode.onDiscard()
                }

                is ActivityFormMode.Update -> {
                    mode.onDiscard()
                }
            }
        },
    )


    Scaffold(
        containerColor = StrideTheme.colorScheme.surface,
        topBar = {
            CustomLeftTopAppBar(
                title = title,
                navigationIcon = {
                    IconButton(
                        onClick = { dismissAction() }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.park_down_icon),
                            contentDescription = "Hide",
                            tint = StrideTheme.colorScheme.onBackground,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.uploadImages(previewImage, context, onFinish = {
                                when (mode) {
                                    is ActivityFormMode.Create -> {
                                        mode.onCreate(state.createActivityDto, selectedSport)
                                    }

                                    is ActivityFormMode.Update -> {
                                        mode.onUpdate(state.updateActivityDto, selectedSport)
                                    }
                                }
                            })
                        },
                        enabled = !isSaving
                    ) {
                        Text(text = primaryActionLabel, style = StrideTheme.typography.titleMedium)
                    }
                }
            )
        },
        bottomBar = {
            val buttonText = when (mode) {
                is ActivityFormMode.Create -> "Discard Activity"
                is ActivityFormMode.Update -> "Discard Unsaved Changed"
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars),
                Alignment.Center
            ) {
                OutlinedButton(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    border = BorderStroke(1.dp, StrideTheme.colorScheme.error),
                    onClick = { isShowDiscardDialog = true },
                ) {
                    Text(
                        buttonText,
                        style = StrideTheme.typography.titleMedium.copy(color = StrideTheme.colorScheme.error)
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = modifier.padding(top = padding.calculateTopPadding() + 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                CustomOutlinedTextField(
                    value = when (mode) {
                        is ActivityFormMode.Create -> state.createActivityDto.name
                        is ActivityFormMode.Update -> state.updateActivityDto.name
                    },
                    singleLine = true,
                    onValueChange = { viewModel.updateName(it) },
                    placeholder = {
                        Text(
                            "Title of your run",
                            style = StrideTheme.typography.labelLarge.copy(color = StrideTheme.colors.placeHolderText)
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                //Choose Sport
                Box {
                    ChooseSportInActivity(
                        modifier = Modifier.height(56.dp),
                        sport = selectedSport,
                        onClick = { showSportBottomSheet = true }
                    )
                    SportBottomSheetWithCategory(
                        sportsByCategory = sportsByCategory,
                        selectedSport = selectedSport,
                        visible = showSportBottomSheet,
                        onItemClick = { sport -> selectedSport = sport },
                        dismissAction = { showSportBottomSheet = false }
                    )
                }

                //Photo Section
                //ShowSelectedImage&ImagePicker
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    val screenWidth = localConfig.screenWidthDp.dp
                    val halfWidth = (screenWidth - 24.dp) / 2

                    if (sport.sportMapType != SportMapType.NO_MAP) {
                        item {
                            Box(
                                modifier = Modifier
                                    .height(160.dp)
                                    .width(halfWidth),
                                Alignment.Center
                            ) {
                                when (mode) {
                                    is ActivityFormMode.Create ->
                                        Image(
                                            modifier = Modifier.fillMaxSize(),
                                            painter = rememberAsyncImagePainter(R.drawable.map_sample_with_noti),
                                            contentDescription = "Sample map"
                                        )

                                    is ActivityFormMode.Update ->
                                        AsyncImage(
                                            modifier = Modifier.fillMaxSize(),
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(mode.activity.mapImage)
                                                .error(R.drawable.image_icon)
                                                .fallback(R.drawable.image_icon)
                                                .placeholder(R.drawable.image_icon)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = "Sample map"
                                        )
                                }
                            }
                        }
                    }

                    if (mode is ActivityFormMode.Update) {
                        items(mode.activity.images) { image ->
                            Image(
                                painter = rememberAsyncImagePainter(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(image)
                                        .error(R.drawable.image_icon)
                                        .fallback(R.drawable.image_icon)
                                        .placeholder(R.drawable.image_icon)
                                        .crossfade(true)
                                        .build()
                                ),
                                contentDescription = null,
                                modifier = Modifier
                                    .height(140.dp)
                                    .width(80.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .border(
                                        1.dp,
                                        StrideTheme.colors.grayBorder,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = ripple()
                                    ) {
                                        val newImages = mode.activity.images.toMutableList()
                                        newImages.remove(image)
                                        viewModel.updateActivityImage(newImages)
                                    },
                                contentScale = ContentScale.Inside
                            )
                        }
                    }

                    itemsIndexed(previewImage) { index, image ->
                        Image(
                            painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(LocalContext.current)
                                    .data(image)
                                    .error(R.drawable.image_icon)
                                    .fallback(R.drawable.image_icon)
                                    .placeholder(R.drawable.image_icon)
                                    .crossfade(true)
                                    .build()
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .height(140.dp)
                                .width(80.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .border(
                                    1.dp,
                                    StrideTheme.colors.grayBorder,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple()
                                ) {
                                    selectedPreviewImageIndex.value = index
                                    expandImageOptionMenu.value = true
                                },
                            contentScale = ContentScale.FillHeight
                        )
                    }

                    item {
                        ImagePickerView(
                            modifier = Modifier
                                .height(160.dp)
                                .width(halfWidth - 24.dp)
                        ) { uri ->
                            previewImage.add(uri)
                        }
                    }
                }

                //Activity Feeling
                //Feeling value view
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            BorderStroke(1.dp, StrideTheme.colors.grayBorder),
                            RoundedCornerShape(8.dp)
                        )
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true)
                        ) {
                            feelingBottomSheetState.show()
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.padding(start = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (state.isRpeChanged || mode is ActivityFormMode.Update) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = rememberAsyncImagePainter(R.drawable.speedometer_outline_icon),
                                contentDescription = "Rpe Status",
                                tint = StrideTheme.colorScheme.onBackground
                            )
                            Spacer(Modifier.width(8.dp))
                            val rpe =
                                when (mode) {
                                    is ActivityFormMode.Create -> state.createActivityDto.rpe
                                    is ActivityFormMode.Update -> state.updateActivityDto.rpe
                                }
                            Text(rpe.toRpeString(), style = StrideTheme.typography.labelLarge)
                        } else {
                            Text(
                                "How did that activity feel?",
                                style = StrideTheme.typography.labelLarge.copy(color = StrideTheme.colors.placeHolderText)
                            )
                        }
                    }
                    IconButton(
                        modifier = Modifier.padding(end = 8.dp),
                        onClick = { feelingBottomSheetState.show() }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.park_down_icon),
                            contentDescription = "Show rpe menu",
                            tint = StrideTheme.colorScheme.onBackground,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                //Description
                CustomOutlinedTextField(
                    value = when (mode) {
                        is ActivityFormMode.Create -> state.createActivityDto.description
                        is ActivityFormMode.Update -> state.updateActivityDto.description
                    },
                    onValueChange = { viewModel.updateDescription(it) },
                    placeholder = {
                        Text(
                            "Jot down note here",
                            style = StrideTheme.typography.labelLarge.copy(color = StrideTheme.colors.placeHolderText)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 120.dp)
                )
            }
        }
    }
    ImageOptionBottomView(
        showImageOption = expandImageOptionMenu.value,
        dismissAction = {
            expandImageOptionMenu.value = false
            selectedPreviewImageIndex.value = null
        },
        onDelete = {
            selectedPreviewImageIndex.value?.let { previewImage.removeAt(it) }
            selectedPreviewImageIndex.value = null
            expandImageOptionMenu.value = false
        }
    )

    RateFeelingBottomSheet(
        value = when (mode) {
            is ActivityFormMode.Create -> state.createActivityDto.rpe
            is ActivityFormMode.Update -> state.updateActivityDto.rpe
        },
        onValueChange = { viewModel.updateFeelingRate(it) }
    )

    if (state.isUploadImage || state.isLoading) {
        Loading()
    }
}

@Composable
private fun ImageOptionBottomView(
    modifier: Modifier = Modifier,
    showImageOption: Boolean = false,
    dismissAction: () -> Unit,
    onDelete: () -> Unit,
) {
    AnimatedVisibility(
        visible = showImageOption,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(durationMillis = 300)
        ),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(durationMillis = 300)
        )
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(StrideTheme.colors.gray.copy(alpha = 0.5f))
                .windowInsetsPadding(WindowInsets.navigationBars)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    dismissAction()
                },
            Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(StrideTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple()
                        ) {
                            onDelete()
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = StrideTheme.colorScheme.onBackground
                    )
                    Spacer(Modifier.width(16.dp))
                    Text("Delete", style = StrideTheme.typography.titleMedium)
                }
            }
        }
    }
}