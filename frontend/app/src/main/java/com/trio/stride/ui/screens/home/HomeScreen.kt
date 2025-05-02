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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trio.stride.base.Resource
import com.trio.stride.data.datastoremanager.TokenManager
import com.trio.stride.data.datastoremanager.UserManager
import com.trio.stride.domain.model.UserInfo
import com.trio.stride.domain.usecase.auth.LogoutUseCase
import com.trio.stride.ui.components.Loading
import com.trio.stride.ui.components.sport.ChooseSportIconButton
import com.trio.stride.ui.components.sport.ChooseSportInSearch
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
                "https://pixsector.com/cache/517d8be6/av5c8336583e291842624.png"
            )
            ChooseSportInSearch("https://pixsector.com/cache/517d8be6/av5c8336583e291842624.png")
//            SportBottomSheet(
//                onItemClick = { sport -> TODO() }
//            )
        }
    }
}


@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val logoutUseCase: LogoutUseCase,
    private val userManager: UserManager
) : ViewModel() {

    private val _logoutSuccess = MutableStateFlow(false)
    val logoutSuccess: StateFlow<Boolean> = _logoutSuccess

    private val _isLoggingOut = MutableStateFlow(false)
    val isLoggingOut: StateFlow<Boolean> = _isLoggingOut

    private val _userInfo = MutableStateFlow(UserInfo())
    val userInfo: StateFlow<UserInfo> = _userInfo

    val errorMessage = mutableStateOf("")

    init {
        getUser()
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