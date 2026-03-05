package com.example.mindstack.ui

import android.app.Application
import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindstack.data.AppDatabase
import com.example.mindstack.data.RetrofitClient
import com.example.mindstack.data.entities.User
import com.example.mindstack.data.network.LoginRequest
import com.example.mindstack.data.network.RegisterRequest
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val mindStackDao = db.mindStackDao()
    private val sharedPreferences = application.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    var currentUser by mutableStateOf<User?>(null)
    var errorMessage by mutableStateOf<String?>(null) // Público para LoginView/RegisterView
    var isLoading by mutableStateOf(false)           // Público para LoginView/RegisterView
    var loginSuccess by mutableStateOf(false)
    var token by mutableStateOf(sharedPreferences.getString("auth_token", "") ?: "")

    init { checkSession() }

    private fun checkSession() {
        val email = sharedPreferences.getString("user_email", null)
        if (email != null) {
            viewModelScope.launch {
                currentUser = mindStackDao.getUserByEmail(email)
                if (currentUser != null) loginSuccess = true
            }
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.authService.login(LoginRequest(email, pass))
                if (response.isSuccessful && response.body() != null) {
                    val authData = response.body()!!
                    val user = User(id = authData.userId, name = authData.name, email = email, password = pass, lastName = "", dateOfBirth = "", gender = "", idRol = 1, idealSleepHours = 8f)
                    mindStackDao.insertUser(user)
                    token = authData.token
                    sharedPreferences.edit().putString("user_email", email).putString("auth_token", token).apply()
                    currentUser = user
                    loginSuccess = true
                } else { errorMessage = "Credenciales incorrectas" }
            } catch (e: Exception) { errorMessage = "Error de red o Timeout" }
            finally { isLoading = false }
        }
    }

    fun registerUser(name: String, lastName: String, email: String, pass: String, dob: String, gender: String, idealSleep: Double) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val request = RegisterRequest(name, lastName, email, pass, dob, gender, idealSleep)
                val response = RetrofitClient.authService.register(request)
                if (response.isSuccessful && response.body() != null) {
                    val authData = response.body()!!
                    val user = User(id = authData.userId, name = name, email = email, password = pass, lastName = lastName, dateOfBirth = dob, gender = gender, idRol = 1, idealSleepHours = idealSleep.toFloat())
                    mindStackDao.insertUser(user)
                    token = authData.token
                    sharedPreferences.edit().putString("user_email", email).putString("auth_token", token).apply()
                    currentUser = user
                    loginSuccess = true
                }
            } catch (e: Exception) { errorMessage = e.message }
            finally { isLoading = false }
        }
    }

    fun logout() {
        currentUser = null
        loginSuccess = false
        token = ""
        sharedPreferences.edit().clear().apply()
    }
}