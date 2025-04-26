package com.trio.stride.ui.components.sport.bottomsheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.trio.stride.ui.theme.StrideTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SportBottomSheet(
    modifier: Modifier = Modifier,
    state: SportBottomSheetState = hiltViewModel(),
) {
    val categories by state.categories.collectAsStateWithLifecycle()
    val sportsByCategory by state.sportsByCategory.collectAsStateWithLifecycle()
    val isError by state.isError.collectAsStateWithLifecycle()
    val errorMessage by state.errorMessage.collectAsStateWithLifecycle()
    val showBottomSheet by state.showBottomSheet.collectAsStateWithLifecycle()

    AnimatedVisibility(
        visible = showBottomSheet,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(durationMillis = 300)
        ),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(durationMillis = 300)
        )
    ) {
        ModalBottomSheet(
            onDismissRequest = { state.hide() },
            modifier = modifier.zIndex(10000f)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                item {
                    Text(
                        "Choose a Sport",
                        style = StrideTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    HorizontalDivider()
                }

                items(categories) { category ->
                    val sports = sportsByCategory[category.id] ?: emptyList()
                    Column(
                        modifier = Modifier
                    ) {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = category.name,
                            modifier = Modifier.fillMaxWidth(),
                            style = StrideTheme.typography.titleMedium
                        )

                        sports.forEach { sport ->
                            Spacer(Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = sport.image,
                                    modifier = Modifier.size(32.dp),
                                    contentDescription = "Sport image",
                                    contentScale = ContentScale.Fit
                                )
                                Spacer(Modifier.width(12.dp)) // Increased spacing
                                Text(
                                    text = sport.name,
                                    style = StrideTheme.typography.labelLarge.copy(fontSize = 16.sp),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}