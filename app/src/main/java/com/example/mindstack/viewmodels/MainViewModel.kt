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

    var isError by mutableStateOf(false)
    var isLoading by mutableStateOf(false)

    fun fetchDashboard(token: String) {
        if (token.isEmpty()) {
            Log.e("MainViewModel", "Token vacío, no se puede cargar")
            return
        }

        val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"

        viewModelScope.launch {
            isLoading = true
            isError = false
            try {
                Log.d("MainViewModel", "Iniciando petición al dashboard...")
                val response = RetrofitClient.dashboardService.getDashboard(bearerToken)
                if (response.isSuccessful) {
                    dashboardData = response.body()
                    Log.d("MainViewModel", "Datos recibidos correctamente")
                } else {
                    isError = true
                    Log.e("MainViewModel", "Error en respuesta: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                isError = true
                Log.e("MainViewModel", "Fallo total: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    fun getSemaphoreIcon(color: String?): Int = when (color?.lowercase()) {
        "verde", "#4caf50" -> R.drawable.semaforo_verde
        "amarillo", "#ffeb3b" -> R.drawable.semaforo_amarillo
        "rojo", "#f44336" -> R.drawable.semaforo_rojo
        else -> R.drawable.semaforo_verde
    }

    fun getBatteryIcon(percentage: Int): Int = when {
        percentage >= 75 -> R.drawable.bateria_verde
        percentage >= 35 -> R.drawable.bateria_amarilla
        else -> R.drawable.bateria_roja
    }
}
