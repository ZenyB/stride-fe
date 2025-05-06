package com.trio.stride.ui.components.map.routesheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.trio.stride.domain.model.RouteItem

@Composable
fun RouteList(items: List<RouteItem>, onClick: (index: Int) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        itemsIndexed(items) { index, item ->
            RouteItemView(
                item, onClick = {
                    onClick(index)
                },
                modifier = Modifier.shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(16.dp),
                    clip = false
                )
            )
        }
        item {
            Spacer(Modifier.height(0.dp))
        }
    }
}