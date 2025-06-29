package com.trio.stride.ui.components.activity.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.trio.stride.domain.model.HeartRateInfo
import com.trio.stride.ui.theme.StrideTheme
import java.text.DecimalFormat

val redShades = listOf(
    Color(0xFFFDB4B5),
    Color(0xFFF6716D),
    Color(0xFFDF2824),
    Color(0xFFB81506),
    Color(0xFF890E11)
)

@Composable
fun HeartRateZonesChart(
    modifier: Modifier = Modifier,
    items: List<HeartRateInfo>,
    totalTime: Long
) {
    val selectedIndex = remember { mutableStateOf(-1) }

    val viewData = DonutChartDataCollection(
        items.filter { it ->
            it.value > 0
        }
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            "Heart Rate Zones",
            style = StrideTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
        )
        DonutChart(
            modifier = Modifier.padding(bottom = 32.dp),
            data = viewData,
            chartSize = 250.dp,
            totalTime = totalTime,
            selectedIndex = selectedIndex.value,
            onSelectedIndexChange = { selectedIndex.value = it }
        ) { selectedValue ->
            val amount = selectedValue?.value
            val percent =
                if (viewData.totalDuration == 0L) "0%"
                else if (amount == null) "--"
                else
                    DecimalFormat("#0.00")
                        .format((amount.toFloat() * 100 / viewData.totalDuration)) + "%"


            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (selectedValue != null) {
                    Text(
                        selectedValue.name,
                        style = StrideTheme.typography.bodySmall.copy(fontWeight = FontWeight.Light),
                        color = StrideTheme.colors.gray600
                    )
                }
                Text(
                    percent,
                    style = StrideTheme.typography.titleLarge
                )
            }
        }

        HeartZoneGroup(
            options = items,
            selected = selectedIndex.value,
            onClick = { index ->
                if (index < viewData.items.size) {
                    selectedIndex.value = index
                } else {
                    selectedIndex.value = -1
                }
            }
        )
    }
}