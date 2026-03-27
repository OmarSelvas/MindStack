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

    fun fetchDashboard(token: String) {
        if (token.isEmpty()) {
            Log.e("MainViewModel", "Token vacío, no se puede cargar")
            return
        }

        viewModelScope.launch {
            isError = false
            try {
                Log.d("MainViewModel", "Iniciando petición al dashboard...")
                val response = RetrofitClient.dashboardService.getDashboard("Bearer $token")
                if (response.isSuccessful) {
                    dashboardData = response.body()
                    Log.d("MainViewModel", "Datos recibidos: ${dashboardData?.streak?.currentStreak} días")
                } else {
                    isError = true
                    Log.e("MainViewModel", "Error en respuesta: ${response.code()}")
                }
            } catch (e: Exception) {
                isError = true
                Log.e("MainViewModel", "Fallo total: ${e.message}")
            }
        }
    }

    fun getSemaphoreIcon(color: String?): Int = when (color?.lowercase()) {
        "verde" -> R.drawable.semaforo_verde
        "amarillo" -> R.drawable.semaforo_amarillo
        "rojo" -> R.drawable.semaforo_rojo
        else -> R.drawable.semaforo_verde
    }

    fun getBatteryIcon(percentage: Int): Int = when {
        percentage >= 75 -> R.drawable.bateria_verde
        percentage >= 35 -> R.drawable.bateria_amarilla
        else -> R.drawable.bateria_roja
    }
}