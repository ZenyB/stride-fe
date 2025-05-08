package com.trio.stride.ui.components.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.advancedShadow

@Composable
fun StrideDialog(
    visible: Boolean,
    dismiss: () -> Unit,
    title: String? = null,
    subtitle: String? = null,
    description: String? = null,
    dismissText: String? = null,
    destructiveText: String = "",
    doneText: String = "",
    neutralText: String = "",
    done: (() -> Unit)? = null,
    neutral: (() -> Unit)? = null,
    destructive: (() -> Unit)? = null
) {
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        Dialog(
            onDismissRequest = dismiss
        ) {
            Box(
                modifier = Modifier
                    .advancedShadow()
                    .background(StrideTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    title?.let {
                        Text(title, style = StrideTheme.typography.titleLarge)
                    }

                    subtitle?.let {
                        Text(
                            subtitle,
                            style = StrideTheme.typography.titleMedium
                        )
                    }

                    description?.let {
                        Text(
                            description,
                            style = StrideTheme.typography.labelLarge
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End)
                    ) {
                        done?.let {
                            TextButton(
                                onClick = done
                            ) {
                                Text(doneText, style = StrideTheme.typography.titleMedium)
                            }
                        }

                        neutral?.let {
                            TextButton(
                                onClick = neutral,
                                colors = ButtonDefaults.buttonColors().copy(
                                    contentColor = StrideTheme.colors.gray500,
                                    containerColor = StrideTheme.colors.transparent
                                )
                            ) {
                                Text(neutralText, style = StrideTheme.typography.titleMedium)
                            }
                        }

                        dismissText?.let {
                            TextButton(
                                onClick = dismiss,
                                colors = ButtonDefaults.buttonColors().copy(
                                    contentColor = StrideTheme.colors.gray500,
                                    containerColor = StrideTheme.colors.transparent
                                )
                            ) {
                                Text(dismissText, style = StrideTheme.typography.titleMedium)
                            }
                        }

                        destructive?.let {
                            TextButton(
                                onClick = destructive,
                                colors = ButtonDefaults.buttonColors().copy(
                                    contentColor = StrideTheme.colorScheme.error,
                                    containerColor = StrideTheme.colors.transparent
                                )
                            ) {
                                Text(destructiveText, style = StrideTheme.typography.titleMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}