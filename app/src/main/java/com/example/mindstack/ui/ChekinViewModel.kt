package com.example.mindstack.ui

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindstack.data.RetrofitClient
import com.example.mindstack.data.network.DailyCheckinRequest
import kotlinx.coroutines.launch

class CheckInViewModel(application: Application) : AndroidViewModel(application) {
    var isLoading by mutableStateOf(false)
    var checkInSuccess by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // VARIABLES PÚBLICAS PARA LAS VISTAS
    var selectedMoodId by mutableStateOf<Int?>(null)
    var savedSleepStart by mutableStateOf<String?>(null)

    fun updateMood(id: Int) {
        selectedMoodId = id
    }

    // Firma de función sincronizada con MainView
    fun submitDailyCheckIn(isMorning: Boolean, currentTime: String, authViewModel: AuthViewModel, mood: Int) {
        val token = authViewModel.token
        if (token.isEmpty()) {
            errorMessage = "No hay sesión activa"
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                val request = DailyCheckinRequest(
                    sleepStart = if (isMorning) (savedSleepStart ?: "00:00") else currentTime,
                    sleepEnd = if (isMorning) currentTime else "00:00",
                    moodScore = mood
                )
                // Usando la ruta api/v1 que acordamos
                val response = RetrofitClient.checkinService.submitCheckin("Bearer $token", request)
                if (response.isSuccessful) {
                    checkInSuccess = true
                    if (isMorning) savedSleepStart = null
                } else {
                    errorMessage = "Error ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error de red"
            } finally {
                isLoading = false
            }
        }
    }
}