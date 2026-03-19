package com.example.mindstack.viewmodels

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindstack.R
import com.example.mindstack.data.RetrofitClient
import kotlinx.coroutines.launch

data class HistoryItem(
    val displayDate: String,
    val battery: Int,
    val batteryIcon: Int,
    val mood: String,
    val hoursSlept: Float,
    val trafficLightColor: Int,
    val trafficLightName: String,
    val concentration: String = "Media",
    val memory: String = "Media"
)

class HistoryViewModel : ViewModel() {
    var historyList by mutableStateOf<List<HistoryItem>>(emptyList())
    var isLoading by mutableStateOf(false)

    fun loadHistory(token: String) {
        isLoading = true
        viewModelScope.launch {
            try {
                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val response = RetrofitClient.checkinService.getHistory(authHeader)

                if (response.isSuccessful) {
                    val body = response.body() ?: emptyList()
                    val sortedBody = body.sortedByDescending { it.checkinId }
                    val total = sortedBody.size
                    
                    historyList = sortedBody.mapIndexed { index, res ->
                        HistoryItem(
                            displayDate = "Registro #${total - index}",
                            battery = res.batteryCog,
                            batteryIcon = getBatteryIcon(res.batteryCog),
                            mood = when(res.moodScore) {
                                1 -> "Exhausto"
                                2 -> "Triste"
                                3 -> "Neutral"
                                4 -> "Feliz"
                                5 -> "Excelente"
                                else -> "Neutral"
                            },
                            hoursSlept = res.hoursSleep.toFloat(),
                            trafficLightColor = when(res.semaphore.color.lowercase()) {
                                "verde" -> R.drawable.semaforo_verde
                                "amarillo" -> R.drawable.semaforo_amarillo
                                else -> R.drawable.semaforo_rojo
                            },
                            trafficLightName = res.semaphore.label
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    private fun getBatteryIcon(percentage: Int): Int = when {
        percentage >= 75 -> R.drawable.bateria_verde
        percentage >= 35 -> R.drawable.bateria_amarilla
        else -> R.drawable.bateria_roja
    }
}
