package com.trio.stride.ui.components.activity.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.trio.stride.R
import com.trio.stride.ui.theme.StrideTheme

@Composable
fun ActivityActionDropdown(handleDelete: () -> Unit, handleEdit: () -> Unit) {
    var menuExpanded by remember { mutableStateOf(false) }

    Box {
        IconButton(
            onClick = {
                menuExpanded = true
            },
            modifier = Modifier
                .background(
                    color = StrideTheme.colors.white,
                    shape = CircleShape
                )
        ) {
            Icon(
                painter = painterResource(R.drawable.ellipsis_more),
                contentDescription = "Close Sheet"
            )
        }
        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            containerColor = StrideTheme.colorScheme.surface,
        ) {
            DropdownMenuItem(
                text = { Text("Edit Activity") },
                onClick = {
                    menuExpanded = false
                },
            )
            DropdownMenuItem(
                text = { Text("Delete Activity") },
                onClick = {
                    menuExpanded = false
                },
            )
        }
    }
}
