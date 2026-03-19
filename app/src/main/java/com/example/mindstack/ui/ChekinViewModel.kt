package com.example.mindstack.ui

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindstack.data.RetrofitClient
import com.example.mindstack.data.network.DailyCheckinRequest
import kotlinx.coroutines.launch

class CheckInViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("checkin_prefs", Context.MODE_PRIVATE)

    var savedSleepStart by mutableStateOf(prefs.getString("sleep_start", null))
    var selectedMoodId by mutableStateOf(if (prefs.contains("mood_id")) prefs.getInt("mood_id", 3) else null)
    
    var isLoading by mutableStateOf(false)
    var checkInSuccess by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun updateMood(id: Int) { 
        selectedMoodId = id 
        prefs.edit().putInt("mood_id", id).apply()
    }

    fun startSleep(currentTime: String) {
        savedSleepStart = currentTime
        prefs.edit().putString("sleep_start", currentTime).apply()
        checkInSuccess = true 
    }

    fun endSleep(currentTime: String, token: String) {
        val sleepStart = savedSleepStart
        val mood = selectedMoodId ?: 3

        if (sleepStart == null) {
            errorMessage = "No se encontró hora de inicio de sueño"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val request = DailyCheckinRequest(
                    sleepStart = sleepStart,
                    sleepEnd = currentTime,
                    moodScore = mood
                )
                
                Log.d("CHECKIN", "Enviando checkin: $request")
                val response = RetrofitClient.checkinService.submitCheckin(bearerToken, request)
                
                if (response.isSuccessful) {
                    clearLocalData()
                    checkInSuccess = true
                } else {
                    errorMessage = "Error en el servidor: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error de conexión: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    private fun clearLocalData() {
        savedSleepStart = null
        selectedMoodId = null
        prefs.edit().remove("sleep_start").remove("mood_id").apply()
    }

    fun submitDailyCheckIn(isMorning: Boolean, currentTime: String, token: String, mood: Int) {
        if (savedSleepStart != null) {
            endSleep(currentTime, token)
        } else {
            startSleep(currentTime)
        }
    }
}
