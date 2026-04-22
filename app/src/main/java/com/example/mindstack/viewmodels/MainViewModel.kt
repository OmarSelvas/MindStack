package com.example.mindstack.viewmodels

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindstack.R
import com.example.mindstack.data.RetrofitClient
import com.example.mindstack.data.network.DashboardResponse
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    var dashboardData by mutableStateOf<DashboardResponse?>(null)
        private set

    // Estados reactivos que la UI observa para mostrar cambios inmediatos
    var currentBattery by mutableStateOf<Int?>(null)
    var currentSemaphoreColor by mutableStateOf<String?>(null)
    var currentSemaphoreLabel by mutableStateOf<String?>(null)
    var currentRecommendation by mutableStateOf<String?>(null)

    var isError by mutableStateOf(false)
    var isLoading by mutableStateOf(false)

    /**
     * Carga el dashboard general y luego fuerza una actualización de la batería
     * en tiempo real para reflejar los juegos jugados durante el día.
     */
    fun fetchDashboard(token: String) {
        if (token.isEmpty()) return

        val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"

        viewModelScope.launch {
            isLoading = true
            isError = false
            try {
                Log.d("MainViewModel", "Actualizando datos del Dashboard...")
                val response = RetrofitClient.checkinService.getDashboard(bearerToken)
                if (response.isSuccessful) {
                    val data = response.body()
                    dashboardData = data
                    
                    // 1. Valores base del dashboard
                    val today = data?.todayCheckin
                    currentBattery = today?.batteryCog ?: data?.weekBatteryAvg?.toInt() ?: 0
                    currentSemaphoreColor = today?.semaphore?.color
                    currentSemaphoreLabel = today?.semaphore?.label
                    currentRecommendation = today?.semaphore?.recommendation ?: "¡Hola! Registra tu sueño para recibir un consejo."

                    // 2. FORZAR ACTUALIZACIÓN: Si hay un checkinId, pedimos la batería combinada
                    // Esto es lo que permite que la batería baje/cambie tras cada juego.
                    val checkinId = today?.checkinId ?: data?.pendingCheckinId
                    if (checkinId != null && checkinId != 0) {
                        fetchRealTimeBattery(bearerToken, checkinId)
                    }
                } else {
                    isError = true
                }
            } catch (e: Exception) {
                isError = true
                Log.e("MainViewModel", "Error fetchDashboard: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Llama a la API de juegos para obtener el cálculo más reciente de la batería.
     */
    private suspend fun fetchRealTimeBattery(token: String, checkinId: Int) {
        try {
            Log.d("MainViewModel", "Consultando batería combinada para checkin: $checkinId")
            val response = RetrofitClient.gameService.getCombinedBattery(token, checkinId)
            
            if (response.isSuccessful) {
                response.body()?.let { batteryData ->
                    // Actualizamos los estados que la MainView está observando
                    currentBattery = batteryData.finalBattery
                    currentSemaphoreColor = batteryData.semaphoreColor
                    currentSemaphoreLabel = batteryData.cognitiveSemaphore
                    currentRecommendation = batteryData.globalRecommendation
                    Log.d("MainViewModel", "Batería actualizada en tiempo real: ${batteryData.finalBattery}%")
                }
            } else {
                Log.e("MainViewModel", "Error al obtener batería real: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("MainViewModel", "Fallo al refrescar batería: ${e.message}")
        }
    }

    /**
     * Retorna el icono del semáforo basado en color, etiqueta, ID o hexadecimal.
     */
    fun getSemaphoreIcon(color: String?, label: String? = null): Int {
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

    fun getBatteryIcon(percentage: Int): Int = when {
        percentage >= 75 -> R.drawable.bateria_verde
        percentage >= 35 -> R.drawable.bateria_amarilla
        else -> R.drawable.bateria_roja
    }
}
