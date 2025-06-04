package com.trio.stride.data.datastoremanager

import com.trio.stride.base.Resource
import com.trio.stride.data.local.dao.CurrentSportDao
import com.trio.stride.data.local.dao.RouteFilterSportDao
import com.trio.stride.data.mapper.roomdatabase.toCurrentSportEntity
import com.trio.stride.data.mapper.roomdatabase.toEntity
import com.trio.stride.data.mapper.roomdatabase.toModel
import com.trio.stride.data.mapper.roomdatabase.toRouteFilterSportEntity
import com.trio.stride.data.mapper.roomdatabase.toSportEntity
import com.trio.stride.domain.model.Sport
import com.trio.stride.domain.model.SportMapType
import com.trio.stride.domain.usecase.sport.GetSportsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SportManager @Inject constructor(
    private val getSportsUseCase: GetSportsUseCase,
    private val currentSportDao: CurrentSportDao,
    private val routeFilterSportDao: RouteFilterSportDao,
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val _sports = MutableStateFlow<List<Sport>>(emptyList())
    val sports: StateFlow<List<Sport>> = _sports

    private val _sportsByCategory = MutableStateFlow<Map<String, List<Sport>>>(emptyMap())
    val sportsByCategory: StateFlow<Map<String, List<Sport>>> = _sportsByCategory

    private val _currentSport = MutableStateFlow<Sport?>(null)
    val currentSport: StateFlow<Sport?> = _currentSport

    private val _routeFilterSport = MutableStateFlow<Sport?>(null)
    val routeFilterSport: StateFlow<Sport?> = _routeFilterSport

    private val _sportsWithMap = MutableStateFlow<List<Sport>>(emptyList())
    val sportsWithMap: StateFlow<List<Sport>> = _sportsWithMap

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        fetchSports()
    }

    private fun shouldFetchSports(lastFetchTime: Long): Boolean {
        val now = System.currentTimeMillis()
        val oneDayMillis = 24 * 60 * 60 * 1000
        return (now - lastFetchTime) > oneDayMillis
    }

    private fun fetchSports() {
        coroutineScope.launch {
            getSportsUseCase.invoke().collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        _isError.value = false
                        _errorMessage.value = null
                        _sports.value = response.data

                        _sportsByCategory.value = response.data.groupBy { it.categoryName }
                        _sportsWithMap.value =
                            response.data.filter { it.sportMapType != SportMapType.NO_MAP }

                        getCurrentSport()
                        getRouteFilterSport()
                    }

                    is Resource.Error -> {
                        handleError(response.error.message)
                    }

                    else -> {
                        Unit
                    }
                }
            }
        }
    }

    private fun getCurrentSport() {
        coroutineScope.launch {
            val localCurrentSport = currentSportDao.getCurrentSport()?.toSportEntity()

            val sports = _sports.value

            if (localCurrentSport == null || sports.none { it.id == localCurrentSport.id }) {
                val defaultSport = sports.firstOrNull()
                defaultSport?.let {
                    val currentSportEntity = it.toEntity().toCurrentSportEntity()
                    currentSportDao.saveCurrentSport(currentSportEntity)
                    _currentSport.value = it
                }
            } else {
                _currentSport.value = localCurrentSport.toModel()
            }
        }
    }

    private fun getRouteFilterSport() {
        coroutineScope.launch {
            val localRouteFilterSport = routeFilterSportDao.getSport()?.toSportEntity()

            val sportsWithMap = _sports.value

            if (localRouteFilterSport == null || sportsWithMap.none { it.id == localRouteFilterSport.id }) {
                val defaultSport = sportsWithMap.firstOrNull()
                defaultSport?.let {
                    val routeFilterSportEntity = it.toEntity().toRouteFilterSportEntity()
                    routeFilterSportDao.saveSport(routeFilterSportEntity)
                    _routeFilterSport.value = it
                }
            } else {
                _routeFilterSport.value = localRouteFilterSport.toModel()
            }
        }
    }

    private fun handleError(message: String?) {
        _isError.value = true
        _errorMessage.value = message
    }

    fun updateCurrentSport(sport: Sport) {
        _currentSport.value = sport
        coroutineScope.launch {
            currentSportDao.deleteCurrentSport()
            currentSportDao.saveCurrentSport(sport.toEntity().toCurrentSportEntity())
        }
    }

    fun updateRouteFilterSport(sport: Sport) {
        _routeFilterSport.value = sport
        coroutineScope.launch {
            routeFilterSportDao.deleteRouteFilterSport()
            routeFilterSportDao.saveSport(sport.toEntity().toRouteFilterSportEntity())
        }
    }

    fun clearSportUserData() {
        coroutineScope.launch {
            currentSportDao.deleteCurrentSport()
            routeFilterSportDao.deleteRouteFilterSport()
        }
    }
}