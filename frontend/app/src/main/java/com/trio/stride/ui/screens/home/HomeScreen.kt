package com.trio.stride.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trio.stride.base.Resource
import com.trio.stride.data.datastoremanager.SportManager
import com.trio.stride.data.datastoremanager.TokenManager
import com.trio.stride.data.datastoremanager.UserManager
import com.trio.stride.domain.model.Category
import com.trio.stride.domain.model.Sport
import com.trio.stride.domain.model.UserInfo
import com.trio.stride.domain.usecase.auth.LogoutUseCase
import com.trio.stride.ui.components.Loading
import com.trio.stride.ui.components.sport.bottomsheet.SportBottomSheetWithCategory
import com.trio.stride.ui.components.sport.bottomsheet.SportMapBottomSheet
import com.trio.stride.ui.components.sport.buttonchoosesport.ChooseSportIconButton
import com.trio.stride.ui.components.sport.buttonchoosesport.ChooseSportInSearch
import com.trio.stride.ui.theme.StrideTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel(),
    onLogOutSuccess: () -> Unit
) {
    val logoutSuccess by viewModel.logoutSuccess.collectAsState()
    val loggingOut by viewModel.isLoggingOut.collectAsState()
    val userInfo by viewModel.userInfo.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val sportsWithMap by viewModel.sportsWithMap.collectAsState()
    val sportsByCategory by viewModel.sportsByCategory.collectAsState()

    var showBottomSheet by remember { mutableStateOf(false) }
    var showBottomSheet2 by remember { mutableStateOf(false) }
    var selectedSport by remember { mutableStateOf(sportsByCategory[categories[0]]?.get(0)) }
    var selectedSport2 by remember { mutableStateOf(sportsWithMap[0]) }

    if (loggingOut) {
        Loading()
    }

    Scaffold { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(userInfo.name, style = StrideTheme.typography.headlineLarge)
            Button(
                onClick = { viewModel.logout() }
            ) {
                Text("Logout", style = StrideTheme.typography.titleMedium)
            }
            Spacer(Modifier.height(8.dp))
            if (viewModel.errorMessage.value != "")
                Text(
                    viewModel.errorMessage.value,
                    style = StrideTheme.typography.bodyMedium,
                    color = StrideTheme.colorScheme.error
                )
            ChooseSportIconButton(
                "https://pixsector.com/cache/517d8be6/av5c8336583e291842624.png",
                onClick = {
                    showBottomSheet = true
                }
            )
            ChooseSportInSearch(
                "https://pixsector.com/cache/517d8be6/av5c8336583e291842624.png",
                onClick = { showBottomSheet2 = true })
            SportBottomSheetWithCategory(
                categories = categories,
                sportsByCategory = sportsByCategory,
                selectedSport = selectedSport!!,
                visible = showBottomSheet,
                onItemClick = { sport -> selectedSport = sport },
                dismissAction = { showBottomSheet = false }
            )
            SportMapBottomSheet(
                sports = sportsWithMap,
                selectedSport = selectedSport2,
                onItemClick = { selectedSport2 = it },
                dismissAction = { showBottomSheet2 = false },
                visible = showBottomSheet2
            )
        }
    }
}


@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val logoutUseCase: LogoutUseCase,
    private val userManager: UserManager,
    private val sportManager: SportManager
) : ViewModel() {

    private val _logoutSuccess = MutableStateFlow(false)
    val logoutSuccess: StateFlow<Boolean> = _logoutSuccess

    private val _isLoggingOut = MutableStateFlow(false)
    val isLoggingOut: StateFlow<Boolean> = _isLoggingOut

    private val _userInfo = MutableStateFlow(UserInfo())
    val userInfo: StateFlow<UserInfo> = _userInfo

    private val _categories = sportManager.categories
    val categories: StateFlow<List<Category>> = _categories

    private val _sportsByCategory = sportManager.sportsByCategory
    val sportsByCategory: StateFlow<Map<Category, List<Sport>>> = _sportsByCategory

    private val _sportsWithMap = sportManager.sportsWithMap
    val sportsWithMap: StateFlow<List<Sport>> = _sportsWithMap

    private val _currentSport = sportManager.currentSport
    val currentSport: StateFlow<Sport> = _currentSport

    val errorMessage = mutableStateOf("")

    init {
        getUser()
    }

    fun updateCurrentSport(sport: Sport) {
        sportManager.updateCurrentSport(sport)
    }

    fun getUser() {
        viewModelScope.launch {
            async { userManager.refreshUser() }.await()
            userManager.getUser().collectLatest { user ->
                user?.let { _userInfo.value = user }
            }
        }
    }


    fun logout() {
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