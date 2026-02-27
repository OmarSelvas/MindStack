package com.example.mindstack.ui

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindstack.data.AppDatabase
import com.example.mindstack.data.entities.User
import com.example.mindstack.data.repository.MindStackRepository
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MindStackRepository
    private val sharedPreferences = application.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    init {
        val dao = AppDatabase.getDatabase(application).mindStackDao()
        repository = MindStackRepository(dao)
        checkSession()
    }

    // Usuario actualmente logueado
    var currentUser by mutableStateOf<User?>(null)

    // Estados para login y registro
    var errorMessage by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)
    var registrationSuccess by mutableStateOf(false)
    var loginSuccess by mutableStateOf(false)

    // Verificar si hay una sesión guardada
    private fun checkSession() {
        val userEmail = sharedPreferences.getString("user_email", null)
        if (userEmail != null) {
            viewModelScope.launch {
                val user = repository.getUserByEmail(userEmail)
                if (user != null) {
                    currentUser = user
                    loginSuccess = true
                }
            }
        }
    }

    // Función para registrar usuario
    fun registerUser(name: String, lastName: String, email: String, password: String, dob: String) {
        if (name.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank() || dob.isBlank()) {
            errorMessage = "Todos los campos son obligatorios"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage = "Correo electrónico no válido"
            return
        }

        if (password.length < 6) {
            errorMessage = "La contraseña debe tener al menos 6 caracteres"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val existingUser = repository.getUserByEmail(email)
                if (existingUser != null) {
                    errorMessage = "Este correo ya está registrado"
                } else {
                    val newUser = User(
                        name = name,
                        lastName = lastName,
                        email = email,
                        password = password,
                        dateOfBirth = dob,
                        gender = "No especificado",
                        idRol = 1,
                        idealSleepHours = 8f
                    )
                    repository.insertUser(newUser)
                    // Auto-login tras registro exitoso
                    currentUser = newUser
                    sharedPreferences.edit().putString("user_email", email).apply()
                    registrationSuccess = true
                    loginSuccess = true
                }
            } catch (e: Exception) {
                errorMessage = "Error al guardar: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // Función para iniciar sesión
    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            errorMessage = "Ingresa tus credenciales"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val user = repository.getUserByEmail(email)
                if (user != null && user.password == pass) {
                    currentUser = user
                    sharedPreferences.edit().putString("user_email", email).apply()
                    loginSuccess = true
                } else {
                    errorMessage = "Correo o contraseña incorrectos"
                }
            } catch (e: Exception) {
                errorMessage = "Error al iniciar sesión"
            } finally {
                isLoading = false
            }
        }
    }
    
    fun clearError() {
        errorMessage = null
    }

    fun logout() {
        currentUser = null
        loginSuccess = false
        sharedPreferences.edit().remove("user_email").apply()
    }
}
