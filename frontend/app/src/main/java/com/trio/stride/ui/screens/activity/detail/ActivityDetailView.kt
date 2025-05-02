package com.trio.stride.ui.screens.activity.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.trio.stride.R
import com.trio.stride.data.dto.CreateActivityRequestDTO
import com.trio.stride.data.dto.UpdateActivityRequestDto
import com.trio.stride.data.mapper.toRpeString
import com.trio.stride.ui.components.CustomLeftTopAppBar
import com.trio.stride.ui.components.activity.feelingbottomsheet.RateFeelingBottomSheet
import com.trio.stride.ui.components.activity.feelingbottomsheet.RateFeelingBottomSheetState
import com.trio.stride.ui.components.sport.ChooseSportInActivity
import com.trio.stride.ui.components.sport.bottomsheet.SportBottomSheet
import com.trio.stride.ui.theme.StrideTheme

@Composable
fun ActivityDetailView(
    title: String,
    primaryActionLabel: String,
    dismissAction: () -> Unit,
    primaryAction: (CreateActivityRequestDTO, UpdateActivityRequestDto) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ActivityDetailViewModel = hiltViewModel(),
    feelingBottomSheetState: RateFeelingBottomSheetState = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val sports by viewModel.sports.collectAsStateWithLifecycle()

    val expandSportMenu = remember { mutableStateOf(false) }
    val feelingBottomSheetState = remember { RateFeelingBottomSheetState() }

    val isCreate = true

    Scaffold(
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
                            primaryAction(
                                state.createActivityDto,
                                state.updateActivityDto
                            )
                        }
                    ) {
                        Text(text = primaryActionLabel, style = StrideTheme.typography.titleMedium)
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = modifier.padding(top = padding.calculateTopPadding() + 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = if (isCreate) state.createActivityDto.name else state.updateActivityDto.name,
                    onValueChange = { viewModel.updateName(it) },
                    placeholder = {
                        Text(
                            "Title of your run",
                            style = StrideTheme.typography.bodyMedium.copy(color = StrideTheme.colors.placeHolderText)
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
//                    keyboardActions = KeyboardActions(
//                        onNext = {
//                            focusRequesterPassword.requestFocus()
//                        }
//                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                //Choose Sport
                Box {
                    ChooseSportInActivity(
                        modifier = Modifier.height(56.dp),
                        sport = state.sport
                    )
                    SportBottomSheet(
                        selectedSport = state.sport,
                        onItemClick = { viewModel.updateSport(it) }
                    )
                }

                //Photo Section

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
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = rememberAsyncImagePainter(state.sport.image), //replace after
                            contentDescription = "Rpe Status",
                            tint = StrideTheme.colorScheme.onBackground
                        )
                        Spacer(Modifier.width(8.dp))
                        val rpe =
                            if (isCreate) state.createActivityDto.rpe else state.updateActivityDto.rpe
                        Text(rpe.toRpeString(), style = StrideTheme.typography.labelMedium)
                    }
                    IconButton(
                        modifier = Modifier.padding(end = 8.dp),
                        onClick = { expandSportMenu.value = true }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.park_down_icon),
                            contentDescription = "Show rpe menu",
                            tint = StrideTheme.colorScheme.onBackground,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                RateFeelingBottomSheet(
                    value = if (isCreate) state.createActivityDto.rpe else state.updateActivityDto.rpe,
                    onValueChange = { viewModel.updateFeelingRate(it) }
                )
                //
            }
        }
    }
}