package com.example.mindstack.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindstack.data.AppDatabase
import com.example.mindstack.data.entities.GameSession
import com.example.mindstack.data.repository.MindStackRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class NeuroReflejoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MindStackRepository
    
    init {
        val dao = AppDatabase.getDatabase(application).mindStackDao()
        repository = MindStackRepository(dao)
    }

    var isGameRunning by mutableStateOf(false)
    var isScreenGreen by mutableStateOf(false)
    var currentTrial by mutableStateOf(0)
    var reactionTimes = mutableListOf<Long>()
    var startTime by mutableStateOf(0L)
    var averageReactionTime by mutableStateOf(0L)
    var isGameOver by mutableStateOf(false)
    var message by mutableStateOf("¡Prepárate!")

    fun startGame() {
        isGameRunning = true
        isGameOver = false
        currentTrial = 1
        reactionTimes.clear()
        startNextTrial()
    }

    private fun startNextTrial() {
        isScreenGreen = false
        message = "Espera al color VERDE..."
        viewModelScope.launch {
            val waitTime = Random.nextLong(2000, 5001) // 2 a 5 segundos
            delay(waitTime)
            if (isGameRunning) {
                isScreenGreen = true
                message = "¡TOCA AHORA!"
                startTime = System.currentTimeMillis()
            }
        }
    }

    fun onScreenTouch(userId: Int) {
        if (!isScreenGreen || isGameOver) return

        val reactionTime = System.currentTimeMillis() - startTime
        reactionTimes.add(reactionTime)
        isScreenGreen = false

        if (currentTrial < 3) {
            currentTrial++
            startNextTrial()
        } else {
            finishGame(userId)
        }
    }

    private fun finishGame(userId: Int) {
        isGameOver = true
        isGameRunning = false
        averageReactionTime = reactionTimes.average().toLong()
        
        // Convertimos el tiempo de reacción en una puntuación de "Batería Cognitiva" (0-100)
        // Por ejemplo: < 250ms = 100%, > 750ms = 0%
        val score = ((1000 - averageReactionTime).coerceIn(0, 1000) / 10).toInt()

        viewModelScope.launch {
            val session = GameSession(
                idUser = userId,
                idJuego = 3, // ID para Neuro-Reflejo
                score = score,
                dateTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
            )
            repository.insertGameSession(session)
        }
    }
}
