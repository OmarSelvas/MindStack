package com.example.mindstack.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindstack.R
import com.example.mindstack.data.AppDatabase
import com.example.mindstack.data.entities.DailyCheckin
import com.example.mindstack.data.entities.User
import com.example.mindstack.data.repository.MindStackRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

enum class TrafficLightStatus(val colorName: String, val color: Color, val iconRes: Int) {
    GREEN("Verde", Color(0xFF4CAF50), R.drawable.semaforo_verde),
    YELLOW("Amarillo", Color(0xFFFFEB3B), R.drawable.semaforo_amarillo),
    RED("Rojo", Color(0xFFF44336), R.drawable.semaforo_rojo),
    UNKNOWN("Desconocido", Color.Gray, R.drawable.semaforo_verde) // Fallback
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MindStackRepository
    
    var trafficLight by mutableStateOf(TrafficLightStatus.UNKNOWN)
    var cognitiveBattery by mutableStateOf(0)
    var sleepDebt by mutableStateOf(0f)
    var hoursSlept by mutableStateOf(0f)
    var adviceMessage by mutableStateOf("Cargando datos...")

    init {
        val dao = AppDatabase.getDatabase(application).mindStackDao()
        repository = MindStackRepository(dao)
    }

    fun loadData(userId: Int, idealHours: Float) {
        viewModelScope.launch {
            repository.getCheckinsByUser(userId).collectLatest { checkins ->
                if (checkins.isNotEmpty()) {
                    val lastCheckin = checkins.first()
                    calculateStatus(lastCheckin, idealHours)
                } else {
                    adviceMessage = "Registra tu estado de ánimo para empezar."
                }
            }
        }
        
        viewModelScope.launch {
            repository.getGameSessionsByUser(userId).collectLatest { sessions ->
                if (sessions.isNotEmpty()) {
                    // Promedio de las últimas 3 sesiones como "Batería Cognitiva"
                    val recentSessions = sessions.take(3)
                    cognitiveBattery = recentSessions.map { it.score }.average().toInt()
                }
            }
        }
    }

    private fun calculateStatus(checkin: DailyCheckin, idealHours: Float) {
        hoursSlept = checkin.hoursSleep
        sleepDebt = checkin.sleepDebt
        
        val sleepPercentage = (checkin.hoursSleep / idealHours) * 100
        val moodValue = checkin.idMood // Asumiendo que idMood 1-5 corresponde a la escala

        trafficLight = when {
            sleepPercentage >= 90 && moodValue >= 4 -> TrafficLightStatus.GREEN
            sleepPercentage >= 70 && moodValue >= 3 -> TrafficLightStatus.YELLOW
            else -> TrafficLightStatus.RED
        }

        updateAdvice()
    }

    private fun updateAdvice() {
        adviceMessage = when {
            trafficLight == TrafficLightStatus.GREEN && cognitiveBattery >= 80 -> 
                "Estás en el momento óptimo para estudiar o realizar tareas difíciles."
            trafficLight == TrafficLightStatus.RED || cognitiveBattery < 50 -> 
                "Tu energía está baja. Necesitas descansar para evitar una crisis."
            else -> 
                "Tu funcionamiento es regular. Realiza tareas ligeras y monitorea tu energía."
        }
    }
}
