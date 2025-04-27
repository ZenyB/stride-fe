package com.trio.stride.data.datastoremanager

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

    private val _categories = MutableStateFlow(emptyList<Category>())
    val categories: StateFlow<List<Category>> = _categories

    private val _sports = MutableStateFlow(emptyList<Sport>())
    val sports: StateFlow<List<Sport>> = _sports

    private val _sportsByCategory = MutableStateFlow(emptyMap<String, List<Sport>>())
    val sportsByCategory: StateFlow<Map<String, List<Sport>>> = _sportsByCategory

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        getSports()
        getCategories()
    }

    private fun getCategories() {
        coroutineScope.launch {
            getCategoriesUseCase.invoke().collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        _isError.value = false
                        _errorMessage.value = null
                        _categories.value = response.data
                    }

                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        _isError.value = true
                        _errorMessage.value = response.error.message
                    }
                }
            }
        }
    }

    private fun getSports() {
        coroutineScope.launch {
            getSportsUseCase.invoke().collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        _isError.value = false
                        _errorMessage.value = null
                        _sports.value = response.data
                        _sportsByCategory.value = response.data.groupBy { it -> it.category.id }
                    }

                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        _isError.value = true
                        _errorMessage.value = response.error.message
                    }
                }
            }
        }
    }
}