package com.trio.stride.data.datastoremanager

import android.util.Log
import com.trio.stride.base.Resource
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
    private val getSportsUseCase: GetSportsUseCase
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
                        Log.i("CATEGORYYY", response.data.toString())
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
                        _currentSport.value = response.data.firstOrNull()
                        Log.i("SPORTTTT", _sportsByCategory.toString())
                    }

                    is Resource.Error -> {
                        handleError(response.error.message)
                        Log.i("SPORTTT_ERROR", response.error.message.toString())
                    }

                    else -> {
                        Unit
                        Log.i("SPORTTT_LOADING", "Loading")
                    }
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
    }
}