package com.example.mindstack.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindstack.R
import com.example.mindstack.data.AppDatabase
import com.example.mindstack.data.entities.DailyCheckin
import com.example.mindstack.data.entities.GameSession
import com.example.mindstack.data.repository.MindStackRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class HistoryItem(
    val date: String,
    val displayDate: String,
    val battery: Int,
    val mood: String,
    val hoursSlept: Float,
    val trafficLightColor: Int,
    val trafficLightName: String,
    val concentration: String = "Media", // Placeholder or derived from games
    val memory: String = "Media"        // Placeholder or derived from games
)

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MindStackRepository
    var historyList by mutableStateOf<List<HistoryItem>>(emptyList())
    var isLoading by mutableStateOf(false)

    init {
        val dao = AppDatabase.getDatabase(application).mindStackDao()
        repository = MindStackRepository(dao)
    }

    fun loadHistory(userId: Int, idealHours: Float) {
        isLoading = true
        viewModelScope.launch {
            combine(
                repository.getCheckinsByUser(userId),
                repository.getGameSessionsByUser(userId)
            ) { checkins, sessions ->
                processHistory(checkins, sessions, idealHours)
            }.collect { processedList ->
                historyList = processedList
                isLoading = false
            }
        }
    }

    private fun processHistory(
        checkins: List<DailyCheckin>,
        sessions: List<GameSession>,
        idealHours: Float
    ): List<HistoryItem> {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEEE dd/MM/yyyy", Locale.getDefault())

        return checkins.map { checkin ->
            val dateStr = checkin.dateTime.split(" ")[0]
            val date = inputFormat.parse(dateStr) ?: Date()
            
            val sleepPercentage = (checkin.hoursSleep / idealHours) * 100
            val moodValue = checkin.idMood

            val (tlName, tlIcon) = when {
                sleepPercentage >= 90 && moodValue >= 4 -> "Verde" to R.drawable.semaforo_verde
                sleepPercentage >= 70 && moodValue >= 3 -> "Amarillo" to R.drawable.semaforo_amarillo
                else -> "Rojo" to R.drawable.semaforo_rojo
            }

            val moodName = when (checkin.idMood) {
                1 -> "Exhausto"
                2 -> "Triste"
                3 -> "Neutral"
                4 -> "Feliz"
                5 -> "Excelente"
                else -> "Neutral"
            }

            HistoryItem(
                date = dateStr,
                displayDate = outputFormat.format(date).replaceFirstChar { it.uppercase() },
                battery = checkin.battery,
                mood = moodName,
                hoursSlept = checkin.hoursSleep,
                trafficLightColor = tlIcon,
                trafficLightName = tlName
            )
        }
    }
}
