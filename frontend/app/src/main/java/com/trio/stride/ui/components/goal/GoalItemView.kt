package com.trio.stride.ui.components.goal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.trio.stride.R
import com.trio.stride.domain.model.GoalHistoryItem
import com.trio.stride.domain.model.GoalItem
import com.trio.stride.domain.model.Sport
import com.trio.stride.domain.model.formatAmount
import com.trio.stride.domain.model.toTitle
import com.trio.stride.ui.theme.StrideTheme

@Composable
fun GoalItemView(item: GoalItem, preview: Boolean = false, onActionClick: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .background(StrideTheme.colorScheme.surface)
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .background(StrideTheme.colorScheme.surface)
                .padding(horizontal = if (preview) 0.dp else 16.dp)
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
                    Text(item.toTitle(), style = StrideTheme.typography.bodyLarge)
                    if (preview) {
                        Text(
                            "${item.formatAmount(item.amountGain)} / ${
                                item.formatAmount(
                                    (item.amountGoal - item.amountGain).coerceAtLeast(0)
                                )
                            }",
                            color = StrideTheme.colors.gray600,
                            style = StrideTheme.typography.bodySmall
                        )

                    } else {
                        Text(item.sport.name, style = StrideTheme.typography.bodyMedium)
                    }
                }

                if (!preview) {
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = onActionClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ellipsis_more),
                            contentDescription = "More Options",
                        )
                    }
                }
            }
            if (!preview) {
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    ColumnText(label = "Current", value = item.formatAmount(item.amountGain))
                    ColumnText(
                        label = "To Go",
                        value = item.formatAmount(
                            (item.amountGoal - item.amountGain).coerceAtLeast(0)
                        )
                    )
                }
            }


            if (!preview) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "History",
                        modifier = Modifier.weight(1f),
                        style = StrideTheme.typography.titleMedium
                    )
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                            contentDescription = if (expanded) "Collapse" else "Expand"
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        if (!preview) {
            AnimatedVisibility(
                visible = expanded, enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .background(StrideTheme.colors.gray300.copy(alpha = 0.2f))
                        .padding(horizontal = 16.dp)
                ) {
                    GoalChart(item)
                }
            }
        }
    }
}

@Composable
fun ColumnText(
    label: String,
    value: String
) {
    Column {
        Text(label, color = StrideTheme.colors.gray600, style = StrideTheme.typography.bodySmall)
        Text(value, style = StrideTheme.typography.titleLarge)
    }
}

val histories: List<GoalHistoryItem> = listOf(
    GoalHistoryItem(
        date = 1736234846000,
        amountGain = 0,
        amountGoal = 10
    ),
    GoalHistoryItem(
        date = 1736839646000,
        amountGain = 0,
        amountGoal = 10
    ),
    GoalHistoryItem(
        date = 1739518046000,
        amountGain = 0,
        amountGoal = 10
    ),
    GoalHistoryItem(
        date = 1741937246000,
        amountGain = 1,
        amountGoal = 10
    ),
    GoalHistoryItem(
        date = 1744615646000,
        amountGain = 0,
        amountGoal = 10
    ),
    GoalHistoryItem(
        date = 1747207646000,
        amountGain = 2,
        amountGoal = 10
    ),
)

val goalItem = GoalItem(
    id = "074f8efc",
    sport = Sport(
        id = "92e5b54a",
        name = "Cycling",
        image = "https://pglijwfxeearqkhmpsdq.supabase.co/storage/v1/object/public/users//15f639c2-5679-40ef-baa2-3cba4af77757.jpg"
    ),
    type = "ACTIVITY",
    timeFrame = "WEEKLY",
    amountGain = 3,
    amountGoal = 10,
    isActive = true,
    histories = histories
)

@Preview(showBackground = true)
@Composable
fun GoalItemPreview() {
    GoalItemView(goalItem, true) {

    }
}
