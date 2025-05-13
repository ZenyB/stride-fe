package com.trio.stride.ui.components.textfield

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.trio.stride.ui.theme.StrideTheme

@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    containerModifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = StrideTheme.typography.labelLarge,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource? = null,
    colors: TextFieldColors? = null,
    shape: Shape = RoundedCornerShape(8.dp),
    errorMessage: String? = null,
    content: @Composable (() -> Unit)? = null
) {
    Column(modifier = containerModifier) {
        OutlinedTextField(
            value = value,
            placeholder = placeholder,
            textStyle = textStyle,
            onValueChange = { onValueChange(it) },
            readOnly = readOnly,
            enabled = enabled,
            label = label,
            prefix = prefix,
            suffix = suffix,
            supportingText = supportingText,
            trailingIcon = trailingIcon,
            leadingIcon = leadingIcon,
            colors = colors ?: TextFieldDefaults.colors().copy(
                unfocusedIndicatorColor = StrideTheme.colors.grayBorder,
                unfocusedContainerColor = StrideTheme.colors.transparent,
                focusedContainerColor = StrideTheme.colors.transparent
            ),
            shape = shape,
            modifier = modifier.fillMaxWidth(),
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardActions = keyboardActions,
            keyboardOptions = keyboardOptions,
            maxLines = maxLines,
            singleLine = singleLine,
            minLines = minLines,
            interactionSource = interactionSource,
        )
        if (isError) {
            Spacer(Modifier.height(4.dp))
            Text(
                errorMessage ?: "Error",
                style = StrideTheme.typography.labelMedium.copy(color = StrideTheme.colorScheme.error)
            )
        }
        if (content != null) {
            content()
        }
    }
}
