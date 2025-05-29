package com.trio.stride.ui.components.goal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trio.stride.domain.model.GoalItem
import com.trio.stride.domain.model.formatAmount
import com.trio.stride.domain.model.getUnit
import com.trio.stride.ui.theme.StrideTheme

@Composable
fun GoalItemHomePreview(item: GoalItem, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(horizontal = 8.dp)
    ) {
        CircularProgressWithImageUrl(
            percentage = (item.amountGain.toFloat() / item.amountGoal),
            imageUrl = item.sport.image,
            modifier = Modifier
                .size(50.dp)
                .padding(4.dp)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            item.formatAmount((item.amountGain).coerceAtLeast(0)),
            style = StrideTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        )
        Text(
            "${
                item.formatAmount(
                    (item.amountGoal).coerceAtLeast(0)
                )
            }/${item.getUnit()}",
            color = StrideTheme.colors.gray600,
            style = StrideTheme.typography.bodySmall
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GoalHomePreview() {
    GoalItemHomePreview(goalItem)
}