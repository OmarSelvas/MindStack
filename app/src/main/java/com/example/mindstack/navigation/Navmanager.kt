package com.example.mindstack.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mindstack.ui.AuthViewModel
import com.example.mindstack.ui.CheckInViewModel
import com.example.mindstack.views.*
import com.example.mindstack.viewmodels.*

@Composable
fun NavManager() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val authViewModel: AuthViewModel = viewModel()
    val checkInViewModel: CheckInViewModel = viewModel()
    val mainViewModel: MainViewModel = viewModel()
    val neuroReflejoViewModel: NeuroReflejoViewModel = viewModel()
    val memoryViewModel: MemoryViewModel = viewModel()
    val historyViewModel: HistoryViewModel = viewModel()

    // Determinamos si el usuario ya está logueado para saltar el login
    val startDestination = if (authViewModel.loginSuccess) "main_view" else "welcome"

    LaunchedEffect(authViewModel.loginSuccess) {
        if (authViewModel.loginSuccess && (currentRoute == "welcome" || currentRoute == "login_view" || currentRoute == "register_view")) {
            navController.navigate("main_view") {
                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
            }
        }
    }

    Scaffold(
        bottomBar = {
            val hideBar = listOf("welcome", "login_view", "register_view")
            if (currentRoute !in hideBar) {
                CustomBottomBar(navController, currentRoute)
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(navController = navController, startDestination = startDestination) {
                composable("welcome") { WelcomeView(navController) }
                composable("login_view") { LoginView(navController, authViewModel) }
                composable("register_view") { RegisterView(navController, authViewModel) }

                composable("main_view") {
                    MainView(navController, authViewModel, checkInViewModel, mainViewModel)
                }
                composable("mood") {
                    MoodView(navController, authViewModel, checkInViewModel)
                }

                composable("profile") { SettingView(navController, authViewModel) }
                composable("list") { GamesView(navController) }

                composable("neuro_reflejo") {
                    NeuroReflejoView(
                        navController = navController,
                        authViewModel = authViewModel,
                        mainViewModel = mainViewModel,
                        viewModel = neuroReflejoViewModel
                    )
                }

                composable("memory_game") {
                    MemoryGameView(
                        navController = navController,
                        authViewModel = authViewModel,
                        mainViewModel = mainViewModel,
                        viewModel = memoryViewModel
                    )
                }

                composable("history") { HistoryView(authViewModel, historyViewModel) }
            }
        }
    }
}

@Composable
fun CustomBottomBar(navController: NavController, currentRoute: String?) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(50.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavBarItem(Icons.AutoMirrored.Filled.List, currentRoute == "list" || currentRoute == "neuro_reflejo" || currentRoute == "memory_game", onClick = { 
                navigateSafely(navController, "list", currentRoute)
            })
            NavBarItem(Icons.Default.DateRange, currentRoute == "mood", onClick = { 
                navigateSafely(navController, "mood", currentRoute)
            })
            NavBarItem(Icons.Default.Home, currentRoute == "main_view", onClick = { 
                navigateSafely(navController, "main_view", currentRoute)
            })
            NavBarItem(Icons.Default.Refresh, currentRoute == "history", onClick = { 
                navigateSafely(navController, "history", currentRoute)
            })
            NavBarItem(Icons.Default.AccountCircle, currentRoute == "profile", onClick = { 
                navigateSafely(navController, "profile", currentRoute)
            })
        }
    }
}

private fun navigateSafely(navController: NavController, route: String, currentRoute: String?) {
    if (route != currentRoute) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}

@Composable
fun NavBarItem(icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0xFFD0E0F0) else Color.Transparent)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.Black)
    }
}
