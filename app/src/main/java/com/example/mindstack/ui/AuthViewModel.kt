package com.example.mindstack.ui

import android.app.Application
import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindstack.data.RetrofitClient
import com.example.mindstack.data.network.LoginRequest
import com.example.mindstack.data.network.RegisterRequest
import com.example.mindstack.data.network.VerifyOtpRequest
import kotlinx.coroutines.launch

// Modelo de Usuario Local
data class User(
    val id: Int,
    val name: String,
    val lastName: String = "",
    val email: String,
    val dateOfBirth: String = "",
    val idealSleepHours: Float = 8.0f
)

// ViewModel
class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    var token by mutableStateOf(prefs.getString("token", "") ?: "")
    var currentUser by mutableStateOf<User?>(null)
    var loginSuccess by mutableStateOf(false)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    var isWaitingForOtp by mutableStateOf(false)
    var temporaryPreAuthToken by mutableStateOf<String?>(null)
    var temporaryEmail by mutableStateOf("")

    // Tutorial flag
    var showTutorial by mutableStateOf(false)

    init {
        restoreSession()
    }

    // Funciones de Sesión
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
            // Verificar si debe mostrar el tutorial al iniciar sesión
            showTutorial = prefs.getBoolean("show_tutorial", true)
        } else {
            loginSuccess = false
        }
    }

    private fun saveAuthData(newToken: String, user: User) {
        token = newToken
        currentUser = user
        loginSuccess = true
        showTutorial = true // Mostramos tutorial en el primer registro/login exitoso
        prefs.edit().apply {
            putString("token", newToken)
            putInt("user_id", user.id)
            putString("user_name", user.name)
            putString("user_lastName", user.lastName)
            putString("user_email", user.email)
            putString("user_dob", user.dateOfBirth)
            putFloat("user_sleep", user.idealSleepHours)
            putBoolean("show_tutorial", true)
            apply()
        }
    }

    fun completeTutorial() {
        showTutorial = false
        prefs.edit().putBoolean("show_tutorial", false).apply()
    }

    private fun clearAuthData() {
        token = ""
        currentUser = null
        loginSuccess = false
        prefs.edit().clear().apply()
    }

    // Funciones de Login y OTP
    fun login(email: String, pass: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.authService.login(LoginRequest(email, pass))
                if (response.isSuccessful) {
                    val body = response.body()
                    temporaryPreAuthToken = body?.preAuthToken
                    temporaryEmail = email
                    isWaitingForOtp = true
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

    fun verifyOtp(code: String, onSuccess: () -> Unit) {
        val preToken = temporaryPreAuthToken ?: return
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.authService.verifyOtp(VerifyOtpRequest(preToken, code))
                if (response.isSuccessful) {
                    val body = response.body()
                    val user = User(
                        id = body?.userId ?: 0,
                        name = body?.name ?: "",
                        lastName = body?.lastName ?: "",
                        email = temporaryEmail,
                        dateOfBirth = body?.dateOfBirth ?: "",
                        idealSleepHours = body?.idealSleepHours?.toFloat() ?: 8.0f
                    )
                    saveAuthData(body?.token ?: "", user)
                    isWaitingForOtp = false
                    onSuccess()
                } else {
                    errorMessage = "Código incorrecto o expirado"
                }
            } catch (e: Exception) {
                errorMessage = "Error de conexión"
            } finally {
                isLoading = false
            }
        }
    }

    fun cancelOtp() {
        isWaitingForOtp = false
        temporaryPreAuthToken = null
    }

    // Registro
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
                    errorMessage = when (response.code()) {
                        409 -> "El correo ya está registrado"
                        400 -> "La contraseña no cumple con los requisitos mínimos"
                        else -> "Error al registrarse (${response.code()})"
                    }
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
