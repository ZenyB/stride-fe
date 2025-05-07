package com.trio.stride.ui.components.map

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.trio.stride.R
import com.trio.stride.ui.components.sport.buttonchoosesport.ChooseSportInSearch
import com.trio.stride.ui.theme.StrideColor
import com.trio.stride.ui.theme.StrideTheme

@Composable
fun SearchFieldWithButton(
    onSearchClick: () -> Unit, chooseSportButton: @Composable () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .clickable {
                onSearchClick()
            }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            chooseSportButton()
            Spacer(
                modifier = Modifier
                    .height(28.dp)
                    .width(1.dp)
                    .background(StrideTheme.colors.gray300)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        onSearchClick()
                    }
            ) {
                Text(
                    "Search locations",
                    color = StrideColor.gray,
                    style = StrideTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(
                modifier = Modifier
                    .height(28.dp)
                    .width(1.dp)
                    .background(StrideTheme.colors.gray300)
            )

            IconButton(
                onClick = {
                },
                modifier = Modifier
                    .background(
                        color = StrideTheme.colors.white,
                        shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                    )
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_save),
                    contentDescription = "Close Sheet"
                )
            }
        }

    }
}

@Preview()
@Composable
fun Preview() {
    SearchFieldWithButton(onSearchClick = {}) {
        ChooseSportInSearch(
            "https://pixsector.com/cache/517d8be6/av5c8336583e291842624.png",
            onClick = {
                
            }
        )
    }
}