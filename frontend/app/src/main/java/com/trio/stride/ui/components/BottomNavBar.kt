package com.trio.stride.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.trio.stride.R
import com.trio.stride.navigation.Screen
import com.trio.stride.ui.theme.StrideTheme
import okhttp3.internal.wait

@Composable
fun BottomNavBar(navController: NavHostController) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    BottomNavigation(backgroundColor = Color.White) {
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
}
