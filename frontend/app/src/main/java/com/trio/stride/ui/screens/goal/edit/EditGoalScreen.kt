package com.trio.stride.ui.screens.goal.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.trio.stride.domain.model.GoalEdit
import com.trio.stride.domain.model.GoalType
import com.trio.stride.ui.components.CustomLeftTopAppBar
import com.trio.stride.ui.components.Loading
import com.trio.stride.ui.components.dialog.StrideDialog
import com.trio.stride.ui.screens.goal.create.SaveGoalState
import com.trio.stride.ui.theme.StrideTheme
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.Locale

@Composable
fun EditGoalScreen(
    navController: NavController,
    data: String,
    viewModel: EditGoalViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val decoded = URLDecoder.decode(data, StandardCharsets.UTF_8.toString())
    val (id, timeFrame, type, amount) = decoded.split("|")

    LaunchedEffect(Unit) {
        viewModel.setGoalEdit(
            GoalEdit(
                id,
                type,
                timeFrame,
                amount.toInt(),
            )
        )
    }

    when (uiState.state) {
        is SaveGoalState.IsSaving -> {
            Loading()
        }

        is SaveGoalState.ErrorSaving -> {
            StrideDialog(
                visible = true,
                title = "Error creating a goal",
                description = (uiState.state as SaveGoalState.ErrorSaving).message,
                dismiss = { viewModel.resetState() },
                dismissText = "OK",
            )
        }

        is SaveGoalState.Success -> {
            navController.popBackStack()
        }

        else -> {

        }
    }

    Scaffold(
        topBar = {
            CustomLeftTopAppBar(
                title = "Edit Goal",
                backgroundColor = StrideTheme.colorScheme.surface,
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
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
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .background(StrideTheme.colorScheme.surface)
            ) {
                Divider(
                    thickness = 1.dp,
                    color = StrideTheme.colors.grayBorder
                )
                Button(
                    onClick = {
                        viewModel.updateGoal()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(vertical = 16.dp)
                        .align(Alignment.Center),
                    enabled = (uiState.amount > 0 && uiState.amount != amount.toInt())
                ) {
                    Text("Save Goal")
                }
            }
        }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(StrideTheme.colorScheme.surface)
                .padding(top = padding.calculateTopPadding())
                .padding(horizontal = 24.dp)
        ) {
            viewModel.goalEdit.value?.type?.let { type ->
                val inputText = uiState.amount.toString()
                val label = when (type) {
                    GoalType.ACTIVITY.name -> "activities"
                    GoalType.DISTANCE.name -> "km"
                    GoalType.TIME.name -> "min"
                    GoalType.ELEVATION.name -> "m"
                    else -> {
                        ""
                    }
                }
                Text(
                    viewModel.goalEdit.value!!.timeFrame.lowercase(Locale.ROOT)
                        .replaceFirstChar { c -> c.uppercaseChar() },
                    style = StrideTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (viewModel.goalEdit.value!!.type == GoalType.TIME.name) {
                    val time = viewModel.getHourAndMinute()
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = time.first.toString(),
                            onValueChange = {
                                it.toIntOrNull()?.let { value ->
                                    viewModel.onHourChanged(value.toString())
                                }
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp),
                            trailingIcon = {
                                Text(
                                    "h",
                                    style = StrideTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(end = 16.dp)
                                )
                            }
                        )

                        OutlinedTextField(
                            value = time.second.toString(),
                            onValueChange = {
                                it.toIntOrNull()?.let { value ->
                                    viewModel.onMinuteChange(value.toString())
                                }
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .offset(x = (-1).dp),
                            shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
                            trailingIcon = {
                                Text(
                                    "m",
                                    style = StrideTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(end = 16.dp)
                                )
                            }
                        )
                    }
                } else {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { viewModel.onAmountInputChanged(it) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            Text(
                                label,
                                style = StrideTheme.typography.bodyMedium,
                                modifier = Modifier.padding(end = 16.dp)
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                    )
                }
                if (uiState.amount <= 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "This value is outside our current limits",
                        style = StrideTheme.typography.bodySmall,
                        color = StrideTheme.colors.red700
                    )
                }

                if (uiState.amount == amount.toInt()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Please enter a new goal value",
                        style = StrideTheme.typography.bodySmall,
                        color = StrideTheme.colors.red700
                    )
                }
            }
        }
    }

}