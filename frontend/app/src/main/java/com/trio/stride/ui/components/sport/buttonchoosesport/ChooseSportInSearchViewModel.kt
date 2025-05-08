package com.trio.stride.ui.components.sport.buttonchoosesport

import androidx.lifecycle.ViewModel
import com.trio.stride.data.datastoremanager.SportManager
import com.trio.stride.domain.model.Sport
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ChooseSportInSearchViewModel @Inject constructor(
    private val sportManager: SportManager
) : ViewModel() {
    private val _sportsWithMap = sportManager.sportsWithMap
    val sportsList: StateFlow<List<Sport>> = _sportsWithMap

    private val _routeFilterSport = sportManager.routeFilterSport
    val selectedSport: StateFlow<Sport?> = _routeFilterSport

    fun selectSport(sport: Sport) {
        sportManager.updateRouteFilterSport(sport)
    }
}