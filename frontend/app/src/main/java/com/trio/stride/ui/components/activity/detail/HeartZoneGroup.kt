package com.trio.stride.ui.components.activity.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.trio.stride.domain.model.HeartRateInfo
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.contrastingTextColor

@Composable
fun HeartZoneGroup(
    options: List<HeartRateInfo>,
    selected: Int,
    onClick: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEachIndexed { index, option ->
            val isSelected = index == selected

            Column(
                modifier = Modifier
                    .weight(1f, true)
                    .fillMaxSize()
                    .clickable {
                        onClick(index)
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f, true)
                        .fillMaxSize()
                        .background(option.color)
                ) {
                    Text(
                        text = option.getDisplayRange(),
                        textAlign = TextAlign.Center,
                        style = StrideTheme.typography
                            .bodyMedium
                            .copy(fontWeight = FontWeight.SemiBold),
                        color = option.color.contrastingTextColor(),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth(),
                    )
                }

                Spacer(
                    modifier = Modifier
                        .height(2.dp)
                        .fillMaxWidth()
                )
                if (isSelected) {
                    Spacer(
                        modifier = Modifier
                            .height(4.dp)
                            .fillMaxWidth()
                            .background(option.color)
                    )
                } else {
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

fun HeartRateInfo.getDisplayRange(): String {
    return when {
        max != null -> "$min–$max"
        else -> "> $min"
    }
}
