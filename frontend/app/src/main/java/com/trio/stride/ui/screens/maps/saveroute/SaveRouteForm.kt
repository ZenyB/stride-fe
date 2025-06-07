package com.trio.stride.ui.screens.maps.saveroute

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.trio.stride.R
import com.trio.stride.domain.model.Sport
import com.trio.stride.ui.components.CustomLeftTopAppBar
import com.trio.stride.ui.components.Loading
import com.trio.stride.ui.components.dialog.StrideDialog
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.formatKmDistance

@Composable
fun SaveRouteForm(
    visible: Boolean,
    routeId: String,
    sport: Sport? = null,
    sportId: String? = null,
    distance: Double,
    mapImage: String?,
    onFinish: () -> Unit,
    saveRouteViewModel: SaveRouteViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    val state by saveRouteViewModel.uiState.collectAsStateWithLifecycle()
    val sportsWithMap by saveRouteViewModel.sportsWithMap.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current

    when (state) {
        is SaveRouteState.ErrorSaving -> {
            StrideDialog(
                visible = true,
                title = "Error saving activity",
                description = (state as SaveRouteState.ErrorSaving).message,
                dismiss = {
                    saveRouteViewModel.resetState()
                    onFinish()
                },
                dismissText = "OK",
            )
        }

        is SaveRouteState.Success -> {
            StrideDialog(
                visible = true,
                title = "Route Saved",
                description = "You can access your Saved Routes from Maps and your profile",
                dismiss = {
                    saveRouteViewModel.resetState()
                    onFinish()
                },
                dismissText = "OK",
            )
        }

        is SaveRouteState.IsSaving -> {
            Loading()
        }

        else -> {

        }
    }


    AnimatedVisibility(
        visible,
        enter = slideInHorizontally(
            initialOffsetX = { it }
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { it }
        )
    ) {
        Scaffold(
            topBar = {
                CustomLeftTopAppBar(
                    title = "Save Route",
                    backgroundColor = StrideTheme.colorScheme.surface,
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                onFinish()
                            },
                            modifier = Modifier
                                .background(
                                    color = StrideTheme.colors.white,
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        TextButton(
                            onClick = {
                                saveRouteViewModel.saveRoute(routeId, name) {
                                    name = ""
                                }

                            },
                            enabled = name.isNotBlank()
                        ) {
                            Text(text = "SAVE", style = StrideTheme.typography.titleMedium)
                        }
                    }
                )
            },
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(StrideTheme.colorScheme.surface)
                    .padding(top = padding.calculateTopPadding())
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(mapImage)
                        .error(R.drawable.image_icon)
                        .fallback(R.drawable.image_icon)
                        .placeholder(R.drawable.image_icon)
                        .crossfade(true)
                        .build(),
                    contentDescription = "image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Sport",
                            style = StrideTheme.typography.bodyMedium,
                            color = StrideTheme.colors.gray600
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        sport?.let {
                            Icon(
                                painter = rememberAsyncImagePainter(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(sport.image)
                                        .error(R.drawable.image_icon)
                                        .fallback(R.drawable.image_icon)
                                        .placeholder(R.drawable.image_icon)
                                        .crossfade(true)
                                        .build()
                                ),
                                modifier = Modifier.size(20.dp),
                                contentDescription = "Sport image",
                            )
                        }
                        sportsWithMap.firstOrNull { item -> item.id == sportId }?.let {
                            Icon(
                                painter = rememberAsyncImagePainter(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(it.image)
                                        .error(R.drawable.image_icon)
                                        .fallback(R.drawable.image_icon)
                                        .placeholder(R.drawable.image_icon)
                                        .crossfade(true)
                                        .build()
                                ),
                                modifier = Modifier.size(20.dp),
                                contentDescription = "Sport image",
                            )
                        }
                    }
                    Column() {
                        Text(
                            "Distance",
                            style = StrideTheme.typography.bodyMedium,
                            color = StrideTheme.colors.gray600
                        )
                        Text(
                            "${formatKmDistance(distance)} km", style = StrideTheme
                                .typography.titleMedium.copy(fontSize = 18.sp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(
                    color = StrideTheme.colors.grayBorder.copy(alpha = 0.35f)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Text("Route Name", style = StrideTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = name,
                        onValueChange = { name = it },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                saveRouteViewModel.saveRoute(routeId, name) {
                                    name = ""
                                }
                            }
                        ),
                    )
                }
            }
        }
    }
}


