package com.example.mindstack.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mindstack.views.LoginView
import com.example.mindstack.views.WelcomeView

@Composable
fun NavManager() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { 
            LoginView(navController)
        }
        composable("welcome") { 
            WelcomeView()
        }
    }
}