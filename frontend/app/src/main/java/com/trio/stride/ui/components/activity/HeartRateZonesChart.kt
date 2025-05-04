package com.trio.stride.ui.components.activity

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
import androidx.compose.ui.tooling.preview.Preview
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
            val amount = selectedValue?.duration
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
                        selectedValue.title,
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

@Preview(showBackground = true)
@Composable
fun HeartRateZonesChartSample() {
    val items = listOf(
        HeartRateInfo(
            max = 112,
            min = 0,
            duration = 42,
            color = StrideTheme.colors.red400,
            title = "Zone 1"
        ),
        HeartRateInfo(
            max = 148,
            min = 112,
            duration = 118,
            color = StrideTheme.colors.red500,
            title = "Zone 2"
        ),
        HeartRateInfo(
            max = 166,
            min = 148,
            duration = 30,
            color = StrideTheme.colors.red600,
            title = "Zone 3"
        ),
        HeartRateInfo(
            max = 184,
            min = 166,
            duration = 215,
            color = StrideTheme.colors.red700,
            title = "Zone 4"
        ),
        HeartRateInfo(
            max = null,
            min = 184,
            duration = 30,
            color = StrideTheme.colors.red800,
            title = "Zone 5"
        )
    )

    HeartRateZonesChart(items = items)

}