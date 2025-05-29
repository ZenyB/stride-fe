package com.trio.stride.ui.components.goal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.trio.stride.domain.model.GoalItem
import com.trio.stride.domain.model.formatAmount
import com.trio.stride.domain.model.toPreviewTitle
import com.trio.stride.ui.theme.StrideTheme

@Composable
fun GoalItemPreview(item: GoalItem, onItemClick: () -> Unit) {
    Column(
        modifier = Modifier
            .background(StrideTheme.colorScheme.surface)
            .fillMaxWidth()
            .clickable { onItemClick() }
            .padding(vertical = 16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .background(StrideTheme.colorScheme.surface)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                CircularProgressWithImageUrl(
                    percentage = (item.amountGain.toFloat() / item.amountGoal),
                    imageUrl = item.sport.image,
                    modifier = Modifier
                        .size(50.dp)
                        .padding(4.dp)
                )

                Column {
                    Text(
                        "${item.toPreviewTitle()} • ${
                            item.formatAmount(
                                (item.amountGoal - item.amountGain).coerceAtLeast(0), true
                            )
                        } to go", style = StrideTheme.typography.bodyLarge
                    )
                    Text(
                        "${item.formatAmount(item.amountGain)} / ${
                            item.formatAmount(
                                (item.amountGoal).coerceAtLeast(0)
                            )
                        }",
                        color = StrideTheme.colors.gray600,
                        style = StrideTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GoalPreview() {
    GoalItemView(goalItem) {}
}
