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
    var loginSuccess by mutableStateOf(false)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    init {
        restoreSession()
    }

    private fun restoreSession() {
        val savedToken = prefs.getString("token", "") ?: ""
        val userId = prefs.getInt("user_id", 0)
        
        if (savedToken.isNotEmpty() && userId != 0) {
            token = savedToken
            currentUser = User(
                id = userId,
                name = prefs.getString("user_name", "") ?: "",
                lastName = prefs.getString("user_lastName", "") ?: "",
                email = prefs.getString("user_email", "") ?: "",
                dateOfBirth = prefs.getString("user_dob", "") ?: "",
                idealSleepHours = prefs.getFloat("user_sleep", 8.0f)
            )
            loginSuccess = true
        } else {
            loginSuccess = false
        }
    }

    private fun saveAuthData(newToken: String, user: User) {
        token = newToken
        currentUser = user
        loginSuccess = true
        prefs.edit().apply {
            putString("token", newToken)
            putInt("user_id", user.id)
            putString("user_name", user.name)
            putString("user_lastName", user.lastName)
            putString("user_email", user.email)
            putString("user_dob", user.dateOfBirth)
            putFloat("user_sleep", user.idealSleepHours)
            apply()
        }
    }

    private fun clearAuthData() {
        token = ""
        currentUser = null
        loginSuccess = false
        prefs.edit().clear().apply()
    }

    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.authService.login(LoginRequest(email, pass))
                if (response.isSuccessful) {
                    val body = response.body()
                    val user = User(
                        id = body?.userId ?: 0,
                        name = body?.name ?: "",
                        lastName = body?.lastName ?: "",
                        email = email,
                        dateOfBirth = body?.dateOfBirth ?: "",
                        idealSleepHours = body?.idealSleepHours?.toFloat() ?: 8.0f
                    )
                    saveAuthData(body?.token ?: "", user)
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
                    val body = response.body()
                    val user = User(
                        id = body?.userId ?: 0, 
                        name = name, 
                        lastName = lastName, 
                        email = email, 
                        dateOfBirth = dob
                    )
                    saveAuthData(body?.token ?: "", user)
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
        clearAuthData()
        onSuccess()
    }
}
