package com.trio.stride.ui.components.textfield

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.trio.stride.R
import com.trio.stride.ui.theme.StrideTheme

@Composable
fun GenderTextField(
    value: String,
    readOnly: Boolean = false,
    enable: Boolean = true,
    colors: TextFieldColors? = null,
    onGenderChange: (String) -> Unit,
) {
    var genderMenuExpanded by remember { mutableStateOf(false) }
    var genderRowSize by remember { mutableStateOf(Size.Zero) }

    Box {
        CustomOutlinedTextField(
            value = value,
            onValueChange = { },
            readOnly = readOnly,
            enabled = enable,
            label = { Text("Gender") },
            trailingIcon = {
                if (!readOnly) {
                    val iconRotationAngle by animateFloatAsState(
                        targetValue = if (genderMenuExpanded) 180f else 0f,
                        animationSpec = tween(durationMillis = 250),
                        label = "DropdownIconRotation"
                    )

                    Icon(
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(iconRotationAngle)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(bounded = true)
                            ) {
                                genderMenuExpanded = true
                            },
                        painter = painterResource(R.drawable.park_down_icon),
                        contentDescription = "Gender dropdown icon",
                        tint = StrideTheme.colorScheme.onBackground
                    )
                }
            },
            colors = colors,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = if (readOnly) null else ripple(bounded = true)
                ) {
                    if (!readOnly) genderMenuExpanded = true
                }
                .onGloballyPositioned { layoutCoordinates ->
                    genderRowSize = layoutCoordinates.size.toSize()
                }
        )
        DropdownMenu(
            modifier = Modifier
                .width(with(LocalDensity.current) { genderRowSize.width.toDp() }),
            shape = RoundedCornerShape(8.dp),
            containerColor = StrideTheme.colorScheme.surface,
            expanded = !readOnly && genderMenuExpanded,
            onDismissRequest = { genderMenuExpanded = false }
        ) {
            listOf("Male", "Female").forEach { gender ->
                val selected = gender == value

                DropdownMenuItem(
                    text = {
                        Text(
                            gender,
                            style = StrideTheme.typography.labelLarge,
                            color = if (selected) StrideTheme.colorScheme.primary else StrideTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        onGenderChange(gender)
                        genderMenuExpanded = false
                    }
                )
            }
        }
    }
}