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
fun GoalItemView(item: GoalItem) {
    var menuExpanded by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .background(StrideTheme.colorScheme.surface)
            .padding(horizontal = 24.dp)
            .padding(vertical = 16.dp)
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
                Text(item.sport.name, style = StrideTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { menuExpanded = true }) {
                Icon(
                    painter = painterResource(id = R.drawable.ellipsis_more),
                    contentDescription = "More Options",
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            ColumnText(label = "Current", value = item.formatAmount(item.amountGain))
            ColumnText(
                label = "To Go",
                value = item.formatAmount((item.amountGoal - item.amountGain))
            )
        }

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

        AnimatedVisibility(
            visible = expanded, enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
//                content()
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

@Preview(showBackground = true)
@Composable
fun GoalItemPreview() {
    val histories: List<GoalHistoryItem> = listOf(
        GoalHistoryItem(
            key = "17/02/2025",
            amountGain = 2,
            amountGoal = 10
        ),
        GoalHistoryItem(
            key = "24/02/2025",
            amountGain = 0,
            amountGoal = 10
        ),
        GoalHistoryItem(
            key = "03/03/2025",
            amountGain = 1,
            amountGoal = 10
        ),
        GoalHistoryItem(
            key = "10/03/2025",
            amountGain = 0,
            amountGoal = 10
        ),
        GoalHistoryItem(
            key = "17/03/2025",
            amountGain = 0,
            amountGoal = 10
        )
    )
    val item = GoalItem(
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

    GoalItemView(item)
}
