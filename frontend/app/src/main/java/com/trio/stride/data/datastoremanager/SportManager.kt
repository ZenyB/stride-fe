package com.trio.stride.data.datastoremanager

import com.trio.stride.base.Resource
import com.trio.stride.data.local.dao.CategoryDao
import com.trio.stride.data.local.dao.CurrentSportDao
import com.trio.stride.data.local.dao.RouteFilterSportDao
import com.trio.stride.data.mapper.roomdatabase.toCurrentSportEntity
import com.trio.stride.data.mapper.roomdatabase.toEntity
import com.trio.stride.data.mapper.roomdatabase.toModel
import com.trio.stride.data.mapper.roomdatabase.toRouteFilterSportEntity
import com.trio.stride.data.mapper.roomdatabase.toSportEntity
import com.trio.stride.domain.model.Category
import com.trio.stride.domain.model.Sport
import com.trio.stride.domain.usecase.category.GetCategoriesUseCase
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
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getSportsUseCase: GetSportsUseCase,
    private val currentSportDao: CurrentSportDao,
    private val routeFilterSportDao: RouteFilterSportDao,
    private val categoryDao: CategoryDao,
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _sports = MutableStateFlow<List<Sport>>(emptyList())
    val sports: StateFlow<List<Sport>> = _sports

    private val _sportsByCategory = MutableStateFlow<Map<Category, List<Sport>>>(emptyMap())
    val sportsByCategory: StateFlow<Map<Category, List<Sport>>> = _sportsByCategory

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
        fetchCategories()
        fetchSports()
    }

    private fun fetchCategories() {
        coroutineScope.launch {
            getCategoriesUseCase().collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        _isError.value = false
                        _errorMessage.value = null
                        _categories.value = response.data
                    }

                    is Resource.Error -> handleError(response.error.message)
                    else -> Unit
                }
            }
        }
    }

    private fun fetchSports() {
        coroutineScope.launch {
            getSportsUseCase.invoke().collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        _isError.value = false
                        _errorMessage.value = null
                        _sports.value = response.data

                        _sportsByCategory.value = response.data.groupBy { it.category }
                        _sportsWithMap.value = response.data.filter { it.sportMapType != null }

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

            if (localCurrentSport == null) {
                val sports = _sports.value
                val currentSportEntity = sports[0].toEntity().toCurrentSportEntity()
                currentSportDao.saveCurrentSport(currentSportEntity)
                _currentSport.value = sports[0]
            } else {
                val category = categoryDao.getCategoryById(localCurrentSport.categoryId)
                category?.let {
                    _currentSport.value = localCurrentSport.toModel(category.toModel())
                }
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
                val category = categoryDao.getCategoryById(localRouteFilterSport.categoryId)
                category?.let {
                    _routeFilterSport.value = localRouteFilterSport.toModel(category.toModel())
                }
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
            currentSportDao.saveCurrentSport(sport.toEntity().toCurrentSportEntity())
        }
    }

    fun updateRouteFilterSport(sport: Sport) {
        _routeFilterSport.value = sport
        coroutineScope.launch {
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