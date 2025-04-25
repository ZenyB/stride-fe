package com.trio.stride.ui.components.map.routesheet

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.trio.stride.domain.model.RouteItem

@Composable
fun RoutePager(
    state: PagerState,
    items: List<RouteItem>,
    onClick: (item: Int) -> Unit,
    modifier: Modifier,
) {
    HorizontalPager(
        state = state,
        contentPadding = PaddingValues(horizontal = 16.dp),
        pageSpacing = 8.dp,
    ) { page ->
        RouteItemView(items[page], onClick = {
            onClick(page)
        }, modifier)
    }
}