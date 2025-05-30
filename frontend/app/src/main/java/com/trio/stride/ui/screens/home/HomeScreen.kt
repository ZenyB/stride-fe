package com.trio.stride.ui.screens.home

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.trio.stride.base.Resource
import com.trio.stride.data.datastoremanager.SportManager
import com.trio.stride.data.datastoremanager.TokenManager
import com.trio.stride.data.repositoryimpl.RecordRepository
import com.trio.stride.data.service.RecordService
import com.trio.stride.domain.model.Sport
import com.trio.stride.domain.model.UserInfo
import com.trio.stride.domain.usecase.auth.LogoutUseCase
import com.trio.stride.domain.usecase.profile.GetUserUseCase
import com.trio.stride.ui.components.Loading
import com.trio.stride.ui.screens.activity.ActivityScreen
import com.trio.stride.ui.screens.goal.view.GoalListHomePreview
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeScreenViewModel = hiltViewModel(),
    onLogOutSuccess: () -> Unit
) {
    val context = LocalContext.current

    val logoutSuccess by viewModel.logoutSuccess.collectAsState()
    val loggingOut by viewModel.isLoggingOut.collectAsState()
    val userInfo by viewModel.userInfo.collectAsState()
    val sportsWithMap by viewModel.sportsWithMap.collectAsState()
    val sportsByCategory by viewModel.sportsByCategory.collectAsState()
    val selectedSport by viewModel.currentSport.collectAsState()
    val selectedSport2 by viewModel.routeFilterSport.collectAsState()

    var showBottomSheet by remember { mutableStateOf(false) }
    var showBottomSheet2 by remember { mutableStateOf(false) }

    if (loggingOut) {
        Loading()
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(bottom = 72.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ActivityScreen(
                navController = navController,
                content = { GoalListHomePreview(navController) }
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}


@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val logoutUseCase: LogoutUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val sportManager: SportManager,
    private val recordRepository: RecordRepository,
) : ViewModel() {

    private val _logoutSuccess = MutableStateFlow(false)
    val logoutSuccess: StateFlow<Boolean> = _logoutSuccess

    private val _isLoggingOut = MutableStateFlow(false)
    val isLoggingOut: StateFlow<Boolean> = _isLoggingOut

    private val _userInfo = MutableStateFlow(UserInfo())
    val userInfo: StateFlow<UserInfo> = _userInfo

    private val _sportsByCategory = sportManager.sportsByCategory
    val sportsByCategory: StateFlow<Map<String, List<Sport>>> = _sportsByCategory

    private val _sportsWithMap = sportManager.sportsWithMap
    val sportsWithMap: StateFlow<List<Sport>> = _sportsWithMap

    private val _currentSport = sportManager.currentSport
    val currentSport: StateFlow<Sport?> = _currentSport

    private val _routeFilterSport = sportManager.routeFilterSport
    val routeFilterSport: StateFlow<Sport?> = _routeFilterSport

    val errorMessage = mutableStateOf("")

    init {
        getUser()
    }

    fun updateCurrentSport(sport: Sport) {
        sportManager.updateCurrentSport(sport)
    }

    fun updateRouteFilterSport(sport: Sport) {
        sportManager.updateRouteFilterSport(sport)
    }

    fun getUser() {
        viewModelScope.launch {
            getUserUseCase.invoke().collectLatest { response ->
                if (response is Resource.Success) {
                    _userInfo.value = response.data
                }
            }
        }
    }


    fun logout(context: Context) {
        val startIntent = Intent(context, RecordService::class.java).apply {
            action = RecordService.STOP_RECORDING
        }
        context.startService(startIntent)

        recordRepository.end()

        viewModelScope.launch {
            _isLoggingOut.value = true

            try {
                logoutUseCase.invoke().collectLatest { response ->
                    when (response) {
                        is Resource.Success -> {
                            tokenManager.clearTokens()

                            _logoutSuccess.value = true

                            errorMessage.value = ""
                        }

                        is Resource.Error -> {
                            errorMessage.value = response.error.message.toString()
                        }

                        is Resource.Loading -> {}
                    }

                }

                _logoutSuccess.value = true

                errorMessage.value = ""
            } catch (e: Exception) {
                errorMessage.value = e.message.toString()
            } finally {
                _isLoggingOut.value = false
            }
        }
    }
}