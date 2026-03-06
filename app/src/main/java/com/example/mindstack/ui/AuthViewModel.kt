package com.example.mindstack.ui

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindstack.data.RetrofitClient
import com.example.mindstack.data.network.LoginRequest
import com.example.mindstack.data.network.RegisterRequest
import kotlinx.coroutines.launch

// MODELO DE USUARIO COMPLETO
data class User(
    val id: Int,
    val name: String,
    val lastName: String = "",
    val email: String,
    val dateOfBirth: String = "",
    val idealSleepHours: Float = 8.0f
)

class AuthViewModel : ViewModel() {
    var token by mutableStateOf("")
    var currentUser by mutableStateOf<User?>(null)
    var loginSuccess by mutableStateOf(false) // REINSTALADO
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.authService.login(LoginRequest(email, pass))
                if (response.isSuccessful) {
                    val body = response.body()
                    token = body?.token ?: ""
                    if (token.isNotEmpty()) {
                        // Mapeo completo para que SettingsView y History no truenen
                        currentUser = User(
                            id = body?.userId ?: 0,
                            name = body?.name ?: "",
                            email = email
                        )
                        loginSuccess = true
                        onSuccess()
                    }
                } else {
                    errorMessage = "Credenciales incorrectas"
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
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
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.authService.register(
                    RegisterRequest(
                        name = name,
                        lastName = lastName,
                        email = email,
                        password = pass,
                        dateOfBirth = dob,
                        gender = gender,
                        idealSleepHours = 8.0
                    )
                )
                if (response.isSuccessful) {
                    token = response.body()?.token ?: ""
                    loginSuccess = true // Para que el NavManager sepa que ya entró
                    onSuccess()
                } else {
                    errorMessage = "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    // MÉTODO LOGOUT REINSTALADO
    fun logout(onSuccess: () -> Unit) {
        token = ""
        currentUser = null
        loginSuccess = false
        onSuccess()
    }
}