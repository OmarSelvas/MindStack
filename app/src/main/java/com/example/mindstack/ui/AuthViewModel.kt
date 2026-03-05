package com.example.mindstack.ui

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
    var errorMessage by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)
    var loginSuccess by mutableStateOf(false)

    init {
        checkSession()
    }

    private fun checkSession() {
        val userEmail = sharedPreferences.getString("user_email", null)
        if (userEmail != null) {
            viewModelScope.launch {
                val user = mindStackDao.getUserByEmail(userEmail)
                if (user != null) {
                    currentUser = user
                    loginSuccess = true
                }
            }
        }
    }

    fun registerUser(
        name: String,
        lastName: String,
        email: String,
        pass: String,
        dob: String,
        gender: String,
        idealSleep: Double
    ) {
        if (name.isBlank() || lastName.isBlank() || email.isBlank() || pass.isBlank() || dob.isBlank()) {
            errorMessage = "Todos los campos son obligatorios"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                // Se construye el request con el endpoint api/v1/auth/register
                val request = RegisterRequest(
                    name = name,
                    lastName = lastName,
                    email = email,
                    password = pass,
                    dateOfBirth = dob,
                    gender = gender,
                    idealSleepHours = idealSleep
                )

                val response = RetrofitClient.authService.register(request)

                if (response.isSuccessful && response.body() != null) {
                    val authData = response.body()!!

                    val newUser = User(
                        id = authData.userId,
                        name = name,
                        lastName = lastName,
                        email = email,
                        password = pass,
                        dateOfBirth = dob,
                        gender = gender,
                        idRol = 1,
                        idealSleepHours = idealSleep.toFloat()
                    )

                    mindStackDao.insertUser(newUser)
                    saveSession(email, authData.token)
                    currentUser = newUser
                    loginSuccess = true
                } else {
                    errorMessage = "Error: El correo ya existe o formato inválido"
                }
            } catch (e: Exception) {
                errorMessage = "Error al conectar con Render (Servidor despertando...)"
            } finally {
                isLoading = false
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
                    var user = mindStackDao.getUserByEmail(email)
                    if (user == null) {
                        user = User(
                            id = authData.userId,
                            name = authData.name,
                            lastName = "",
                            email = email,
                            password = pass,
                            dateOfBirth = "",
                            gender = "",
                            idRol = 1,
                            idealSleepHours = 8f
                        )
                        mindStackDao.insertUser(user)
                    }
                    saveSession(email, authData.token)
                    currentUser = user
                    loginSuccess = true
                } else {
                    errorMessage = "Correo o contraseña incorrectos"
                }
            } catch (e: Exception) {
                errorMessage = "Error de conexión"
            } finally {
                isLoading = false
            }
        }
    }

    private fun saveSession(email: String, token: String) {
        sharedPreferences.edit().apply {
            putString("user_email", email)
            putString("auth_token", token)
            apply()
        }
    }

    fun logout() {
        currentUser = null
        loginSuccess = false
        sharedPreferences.edit().clear().apply()
    }
}