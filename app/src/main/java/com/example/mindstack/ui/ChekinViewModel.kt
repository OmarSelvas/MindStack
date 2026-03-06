package com.example.mindstack.ui

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindstack.data.RetrofitClient
import com.example.mindstack.data.network.DailyCheckinRequest
import kotlinx.coroutines.launch

class CheckInViewModel : ViewModel() {
    var savedSleepStart by mutableStateOf<String?>(null)
    var selectedMoodId by mutableStateOf<Int?>(null)
    var isLoading by mutableStateOf(false)
    var checkInSuccess by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun updateMood(id: Int) { selectedMoodId = id }

    fun submitDailyCheckIn(isMorning: Boolean, currentTime: String, token: String, mood: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            // LOG DE COMBATE: Para ver si el botón sirve
            Log.d("MINDSTACK_URGENTE", "Botón presionado. Token: ${token.take(10)}... Mood: $mood")

            try {
                // FORZAMOS EL OBJETO (Si no hay hora de inicio, ponemos una por defecto para que no truene)
                val request = DailyCheckinRequest(
                    sleepStart = savedSleepStart ?: "23:00",
                    sleepEnd = currentTime,
                    moodScore = mood
                )

                val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"

                Log.d("MINDSTACK_URGENTE", "DISPARANDO PETICIÓN A RENDER...")
                val response = RetrofitClient.checkinService.submitCheckin(bearerToken, request)

                if (response.isSuccessful) {
                    Log.d("MINDSTACK_URGENTE", "¡AL FIN! Render respondió: ${response.body()?.message}")
                    checkInSuccess = true
                    savedSleepStart = null
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("MINDSTACK_URGENTE", "SERVIDOR RECHAZÓ: ${response.code()} - $errorBody")
                    errorMessage = "Error ${response.code()}"
                }
            } catch (e: Exception) {
                Log.e("MINDSTACK_URGENTE", "ERROR DE RED (¿INTERNET?): ${e.message}")
                errorMessage = "Fallo de conexión"
            } finally {
                isLoading = false
            }
        }
    }
}