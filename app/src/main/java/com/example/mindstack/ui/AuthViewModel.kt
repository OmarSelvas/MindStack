package com.example.mindstack.ui

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
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

class AuthViewModel : ViewModel() {
    var token by mutableStateOf("")
    var currentUser by mutableStateOf<User?>(null)
    var loginSuccess by mutableStateOf(false)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.authService.login(LoginRequest(email, pass))
                if (response.isSuccessful) {
                    val body = response.body()
                    token = body?.token ?: ""
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
                }
            } catch (e: Exception) { errorMessage = e.message } finally { isLoading = false }
        }
    }

    fun registerUser(name: String, lastName: String, email: String, pass: String, dob: String, gender: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.authService.register(RegisterRequest(name, lastName, email, pass, dob, gender, 8.0))
                if (response.isSuccessful) {
                    token = response.body()?.token ?: ""
                    currentUser = User(id = response.body()?.userId ?: 0, name = name, lastName = lastName, email = email, dateOfBirth = dob)
                    loginSuccess = true
                    onSuccess()
                }
            } catch (e: Exception) { errorMessage = e.message } finally { isLoading = false }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        token = ""; currentUser = null; loginSuccess = false; onSuccess()
    }
}