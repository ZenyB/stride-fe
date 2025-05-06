package com.trio.stride.ui.components.activity.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.trio.stride.domain.model.HeartRateInfo
import com.trio.stride.ui.theme.StrideTheme
import java.text.DecimalFormat


@Composable
fun HeartRateZonesChart(
    modifier: Modifier = Modifier,
    items: List<HeartRateInfo>
) {
    var selected by remember { mutableStateOf<HeartRateInfo?>(items[0]) }
    val viewData = DonutChartDataCollection(
        items
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
            Modifier.padding(bottom = 32.dp),
            data = viewData,
            chartSize = 250.dp
        ) { selectedValue ->
            selected = selectedValue
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
            selected = selected,
        )
    }
}
