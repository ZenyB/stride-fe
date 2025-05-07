package com.trio.stride.ui.screens.maps.search

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.Point
import com.mapbox.search.autocomplete.PlaceAutocomplete
import com.mapbox.search.autocomplete.PlaceAutocompleteOptions
import com.mapbox.search.autocomplete.PlaceAutocompleteResult
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion
import com.mapbox.search.common.IsoCountryCode
import com.trio.stride.base.BaseViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class SearchMapViewModel(
    private val placeAutocomplete: PlaceAutocomplete
) : BaseViewModel<SearchMapState>() {
    var query by mutableStateOf("")
    private val queryFlow = MutableStateFlow("")

    var suggestions by mutableStateOf<List<PlaceAutocompleteSuggestion>>(emptyList())
    var selectedResult by mutableStateOf<PlaceAutocompleteResult?>(null)

    init {
        viewModelScope.launch {
            queryFlow
                .debounce(300)
                .distinctUntilChanged()
                .collectLatest { debouncedQuery ->
                    if (debouncedQuery.isNotEmpty()) {
                        Log.d("search query", "query: $debouncedQuery")
                        val response = placeAutocomplete
                            .suggestions(
                                query = debouncedQuery,
                                options = PlaceAutocompleteOptions(
                                    countries = listOf(
                                        IsoCountryCode.VIETNAM
                                    )
                                )
                            )
                        suggestions = if (response.isValue) {
                            response.value ?: emptyList()
                        } else {
                            emptyList()
                        }
                    } else {
                        suggestions = emptyList()
                    }
                }
        }
    }

    fun onQueryChanged(newQuery: String) {
        query = newQuery
        queryFlow.value = newQuery
    }

    fun selectSuggestion(suggestion: PlaceAutocompleteSuggestion) {
        viewModelScope.launch {
            val result = placeAutocomplete.select(suggestion)

            result.onValue {
                selectedResult = it
                setState { SearchMapState.Success(it.coordinate) }
            }.onError {
                setState { SearchMapState.Error(it.message ?: "Error selecting a location") }
            }
        }
    }

    fun searchCurrentLocation(point: Point) {
        setState { SearchMapState.Success(point) }
    }

    override fun createInitialState(): SearchMapState {
        return SearchMapState.Idle
    }
}