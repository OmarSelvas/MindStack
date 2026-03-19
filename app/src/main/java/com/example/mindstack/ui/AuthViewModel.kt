package com.example.mindstack.ui

import android.app.Application
import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindstack.data.RetrofitClient
import com.example.mindstack.data.network.LoginRequest
import com.example.mindstack.data.network.RegisterRequest
import kotlinx.coroutines.launch

data class User(
    val id: Int,
    val name: String,
    val lastName: String = "",
    val email: String,
    val dateOfBirth: String = "",
    val idealSleepHours: Float = 8.0f
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    var token by mutableStateOf(prefs.getString("token", "") ?: "")
    var currentUser by mutableStateOf<User?>(null)
    var loginSuccess by mutableStateOf(token.isNotEmpty())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    init {
        // Si hay token, podríamos intentar cargar el perfil o dejarlo así
        if (token.isNotEmpty()) {
            loginSuccess = true
        }
    }

    private fun saveToken(newToken: String) {
        token = newToken
        prefs.edit().putString("token", newToken).apply()
    }

    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.authService.login(LoginRequest(email, pass))
                if (response.isSuccessful) {
                    val body = response.body()
                    val newToken = body?.token ?: ""
                    saveToken(newToken)
                    
                    currentUser = User(
                        id = body?.userId ?: 0,
                        name = body?.name ?: "",
                        lastName = body?.lastName ?: "",
                        email = email,
                        dateOfBirth = body?.dateOfBirth ?: "",
                        idealSleepHours = body?.idealSleepHours?.toFloat() ?: 8.0f
                    )
                    loginSuccess = true
                    onSuccess()
                } else {
                    errorMessage = "Credenciales incorrectas"
                }
            } catch (e: Exception) { 
                errorMessage = "Error de conexión" 
            } finally { 
                isLoading = false 
            }
        }
    }

    fun registerUser(name: String, lastName: String, email: String, pass: String, dob: String, gender: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.authService.register(RegisterRequest(name, lastName, email, pass, dob, gender, 8.0))
                if (response.isSuccessful) {
                    val newToken = response.body()?.token ?: ""
                    saveToken(newToken)
                    currentUser = User(id = response.body()?.userId ?: 0, name = name, lastName = lastName, email = email, dateOfBirth = dob)
                    loginSuccess = true
                    onSuccess()
                } else {
                    errorMessage = "El correo ya está registrado"
                }
            } catch (e: Exception) { 
                errorMessage = "Error de conexión" 
            } finally { 
                isLoading = false 
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        saveToken("")
        currentUser = null
        loginSuccess = false
        onSuccess()
    }
}
