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
import com.trio.stride.ui.utils.getDaysInMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SportManager @Inject constructor(
    private val getSportsUseCase: GetSportsUseCase,
    private val currentSportDao: CurrentSportDao,
    private val routeFilterSportDao: RouteFilterSportDao,
    private val metadataManager: MetadataManager,
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
        coroutineScope.launch {
            fetchSports()
        }
    }

    private suspend fun fetchSports() {
        val lastFetchTime = metadataManager.getLastSportFetchTime().firstOrNull() ?: 0L
        val currentTime = System.currentTimeMillis()
        val shouldFetch = currentTime - lastFetchTime > getDaysInMillis(1)

        getSportsUseCase.invoke(forceRefresh = shouldFetch)
            .retryWhen { cause, attempt ->
                if (attempt < 3 && cause is IOException) {
                    delay(1000L * (attempt + 1))
                    true
                } else false
            }
            .collectLatest { response ->
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

                    else -> Unit
                }
            }
    }

    private fun getCurrentSport() {
        coroutineScope.launch {
            val localCurrentSport = currentSportDao.getCurrentSport()?.toSportEntity()

            if (localCurrentSport == null) {
                val sports = _sports.value
                val currentSportEntity = sports[0].toEntity().toCurrentSportEntity()
                currentSportDao.saveCurrentSport(currentSportEntity)
                _currentSport.value = sports[0]
            } else {
                _currentSport.value = localCurrentSport.toModel()
            }
        }
    }

    private fun getRouteFilterSport() {
        coroutineScope.launch {
            val localRouteFilterSport = routeFilterSportDao.getSport()?.toSportEntity()

            if (localRouteFilterSport == null) {
                val sportsWithMap = _sportsWithMap.value
                val routeFilterSportEntity = sportsWithMap[0].toEntity().toCurrentSportEntity()
                currentSportDao.saveCurrentSport(routeFilterSportEntity)
                _routeFilterSport.value = sportsWithMap[0]
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