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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mindstack.ui.AuthViewModel
import com.example.mindstack.views.LoginView
import com.example.mindstack.views.WelcomeView
import com.example.mindstack.views.MoodView
import com.example.mindstack.views.RegisterView
import com.example.mindstack.views.MainView
import com.example.mindstack.views.SettingView

@Composable
fun NavManager() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val authViewModel: AuthViewModel = viewModel()

    Scaffold(
        bottomBar = {
            val hideBar = listOf("welcome", "login_view", "register_view")
            if (currentRoute !in hideBar) {
                CustomBottomBar(navController, currentRoute)
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(navController = navController, startDestination = "welcome") {
                composable("welcome") { WelcomeView(navController) }
                composable("login_view") { LoginView(navController, authViewModel) }
                composable("register_view") { RegisterView(navController, authViewModel) }
                composable("main_view") { MainView(navController) }
                composable("mood") { MoodView(navController) }
                composable("profile") { SettingView(navController, authViewModel) }
            }
        }
    }
}

@Composable
fun CustomBottomBar(navController: NavController, currentRoute: String?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp), 
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(50.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavBarItem(Icons.AutoMirrored.Filled.List, currentRoute == "list", onClick = {  })
            NavBarItem(Icons.Default.DateRange, currentRoute == "mood", onClick = { navController.navigate("mood") })
            NavBarItem(Icons.Default.Home, currentRoute == "main_view", onClick = { navController.navigate("main_view") })
            NavBarItem(Icons.Default.Refresh, currentRoute == "history", onClick = {  })
            NavBarItem(Icons.Default.AccountCircle, currentRoute == "profile", onClick = { navController.navigate("profile") })
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
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Black
        )
    }
}
