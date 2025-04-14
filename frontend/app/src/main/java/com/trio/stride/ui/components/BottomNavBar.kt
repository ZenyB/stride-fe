package com.trio.stride.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.trio.stride.R
import com.trio.stride.navigation.Screen
import com.trio.stride.ui.theme.StrideTheme

@Composable
fun BottomNavBar(navController: NavHostController) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    Column(modifier = Modifier.background(Color.White)) {
        BottomNavigation(
            backgroundColor = Color.White
        ) {
            Screen.BottomNavScreen.items.forEach { screen ->
                val selected = currentDestination?.route == screen.route

                BottomNavigationItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = screen.icon ?: R.drawable.home),
                            contentDescription = screen.title,
                            tint = if (selected)
                                StrideTheme.colorScheme.primary
                            else
                                Color.Black
                        )
                    },
                    label = {
                        Text(
                            text = screen.title,
                            style = if (selected)
                                StrideTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            else
                                StrideTheme.typography.labelMedium,
                            color = if (selected)
                                StrideTheme.colorScheme.primary
                            else
                                Color.Black
                        )
                    },
                    selected = selected,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(Screen.BottomNavScreen.Home.route)
                            launchSingleTop = true
                        }
                    },
                    selectedContentColor = StrideTheme.colorScheme.primary,
                    unselectedContentColor = Color.Black,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
                .background(Color.White)
        )
    }
}
