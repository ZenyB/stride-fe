package com.trio.stride.ui.screens.maps.search

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Point
import com.mapbox.search.autocomplete.PlaceAutocomplete
import com.trio.stride.R
import com.trio.stride.ui.theme.StrideColor
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.map.LocationUtils
import kotlinx.coroutines.delay

@Composable
fun SearchMapScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val placeAutocomplete = remember {
        PlaceAutocomplete.create()
    }

    val viewModel = remember { SearchMapViewModel(placeAutocomplete) }
    val query = viewModel.query
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)


    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(200)
        focusRequester.requestFocus()
    }

    when (uiState) {
        is SearchMapState.Success -> {
            val point = (uiState as SearchMapState.Success).point
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("selected_point", point)

            navController.popBackStack()

        }

        else -> {
        }
    }

    Scaffold() { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(StrideTheme.colorScheme.surfaceContainerLowest)
                .padding(top = padding.calculateTopPadding())
                .padding(bottom = padding.calculateBottomPadding())
        ) {
            Row(
                Modifier.padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { viewModel.onQueryChanged(it) },
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                        .focusRequester(focusRequester),
                    placeholder = { Text("Search by keyword", color = StrideColor.gray) },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon",
                            tint = Color.Black
                        )
                    },
                )
                TextButton(
                    onClick = {
                        navController.popBackStack()
                    }
                ) {
                    Text(
                        "Cancel",
                        style = StrideTheme.typography.bodyMedium,
                        color = StrideTheme.colors.red800
                    )
                }
            }


            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                item {
                    Row(modifier = Modifier
                        .clickable {
                            LocationUtils.getCurrentLocation(
                                fusedLocationClient,
                                context
                            ) { point ->
                                viewModel.searchCurrentLocation(point)
                            }
                        }
                        .padding(vertical = 12.dp, horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(
                            modifier = Modifier.size(28.dp),
                            painter = painterResource(R.drawable.user_location_icon),
                            contentDescription = "User location",
                        )
                        Text(
                            text = "Current Location",
                            style = StrideTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth(),
                        )
                    }
                }
                itemsIndexed(viewModel.suggestions) { index, suggestion ->
                    Column(modifier = Modifier
                        .clickable {
                            viewModel.selectSuggestion(suggestion)
                        }
                        .padding(vertical = 12.dp, horizontal = 24.dp)) {
                        Text(
                            text = suggestion.name,
                            style = StrideTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth(),
                        )
                        Spacer(Modifier.height(8.dp))
                        suggestion.formattedAddress?.let {
                            Text(
                                text = it,
                                style = StrideTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }

}
