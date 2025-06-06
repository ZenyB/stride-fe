package com.trio.stride.ui.components.textfield

import android.app.DatePickerDialog
import android.view.ContextThemeWrapper
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.trio.stride.R
import com.trio.stride.ui.theme.StrideTheme
import java.time.LocalDate

@Composable
fun CalendarTextField(
    value: String,
    initialDate: LocalDate = LocalDate.now(),
    label: @Composable (() -> Unit)? = { Text("Dob") },
    readOnly: Boolean = false,
    enable: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    colors: TextFieldColors? = null,
    onDateChange: (String) -> Unit,
) {
    val context = LocalContext.current

    fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            ContextThemeWrapper(context, R.style.CustomDatePickerDialogTheme),
            { _, selectedYear, selectedMonth, selectedDay ->
                val formatted =
                    "%02d/%02d/%04d".format(selectedDay, selectedMonth + 1, selectedYear)
                onDateChange(formatted)
            },
            initialDate.year,
            initialDate.monthValue - 1,
            initialDate.dayOfMonth
        )
        datePickerDialog.show()
    }

    CustomOutlinedTextField(
        value = value,
        onValueChange = { },
        readOnly = true,
        enabled = enable,
        label = label,
        trailingIcon = {
            if (!readOnly) {
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true)
                        ) {
                            showDatePicker()
                        },
                    imageVector = Icons.Outlined.DateRange,
                    contentDescription = "Calendar icon",
                    tint = StrideTheme.colorScheme.onBackground
                )
            }
        },
        isError = isError,
        errorMessage = errorMessage,
        colors = colors,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = if (readOnly) null else ripple(bounded = true)
            ) {
                if (!readOnly) showDatePicker()
            },
    )
}