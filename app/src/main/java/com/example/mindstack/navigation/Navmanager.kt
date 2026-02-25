package com.example.mindstack.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mindstack.views.LoginView
import com.example.mindstack.views.WelcomeView
import com.example.mindstack.views.MoodView
import com.example.mindstack.views.RegisterView

@Composable
fun NavManager() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") { 
            WelcomeView(navController)
        }
        composable("login_view") { 
            LoginView(navController)
        }
        composable("register_view") { 
            RegisterView(navController)
        }
        composable("mood") {
            MoodView(navController)
        }
    }
}