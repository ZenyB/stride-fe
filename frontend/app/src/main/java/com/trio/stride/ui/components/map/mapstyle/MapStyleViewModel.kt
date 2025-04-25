package com.trio.stride.ui.components.map.mapstyle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.maps.Style
import com.trio.stride.data.datastoremanager.MapStyleManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapStyleViewModel @Inject constructor(
    private val mapStyleManager: MapStyleManager
) : ViewModel() {
    private val _mapStyle = MutableStateFlow(Style.MAPBOX_STREETS)
    val mapStyle: StateFlow<String> = _mapStyle.asStateFlow()

    init {
        viewModelScope.launch {
            mapStyleManager.getMapStyle()
                .collectLatest { style ->
                    _mapStyle.value = if (style.isNullOrEmpty()) {
                        Style.MAPBOX_STREETS
                    } else {
                        style
                    }
                }
        }
    }

    fun selectStyle(style: String) {
        if (_mapStyle.value != style) {
            _mapStyle.value = style
            viewModelScope.launch {
                mapStyleManager.saveMapStyle(style)
            }
        }
    }
}