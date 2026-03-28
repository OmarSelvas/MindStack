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
                            trafficLightColor = getSemaphoreIcon(res.semaphore.color, res.semaphore.label),
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

    /**
     * Retorna el icono del semáforo basado en color, etiqueta, ID o hexadecimal.
     */
    private fun getSemaphoreIcon(color: String?, label: String? = null): Int {
        val c = color?.lowercase()?.trim() ?: ""
        val l = label?.lowercase()?.trim() ?: ""

        return when {
            // VERDE: 1, Verde, Estable, #4caf50
            c == "1" || l == "1" || c.contains("verde") || l.contains("verde") || 
            c == "estable" || l == "estable" || c == "#4caf50" -> R.drawable.semaforo_verde

            // AMARILLO: 2, Amarillo, Alerta, #ffeb3b, #ffc107
            c == "2" || l == "2" || c.contains("amarillo") || l.contains("amarillo") || 
            c == "alerta" || l == "alerta" || c == "#ffeb3b" || c == "#ffc107" -> R.drawable.semaforo_amarillo

            // ROJO: 3, Rojo, Critico, #f44336
            c == "3" || l == "3" || c.contains("rojo") || l.contains("rojo") || 
            c.contains("critico") || l.contains("critico") || c == "#f44336" -> R.drawable.semaforo_rojo

            else -> R.drawable.semaforo_verde
        }
    }

    private fun getBatteryIcon(percentage: Int): Int = when {
        percentage >= 75 -> R.drawable.bateria_verde
        percentage >= 35 -> R.drawable.bateria_amarilla
        else -> R.drawable.bateria_roja
    }
}
