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

class WorkingMemoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MindStackRepository
    
    init {
        val dao = AppDatabase.getDatabase(application).mindStackDao()
        repository = MindStackRepository(dao)
    }

    var sequence by mutableStateOf(listOf<Int>())
    var userSequence by mutableStateOf(listOf<Int>())
    var isShowingSequence by mutableStateOf(false)
    var highlightedIndex by mutableStateOf(-1)
    var isGameOver by mutableStateOf(false)
    var hits by mutableStateOf(0)
    var totalTouchesRequired by mutableStateOf(5)
    var precision by mutableStateOf(0f)
    var message by mutableStateOf("¡Prepárate!")

    fun startGame() {
        generateSequence()
        userSequence = emptyList()
        hits = 0
        precision = 0f
        isGameOver = false
        showSequence()
    }

    private fun generateSequence() {
        sequence = List(5) { Random.nextInt(0, 9) }
    }

    private fun showSequence() {
        viewModelScope.launch {
            isShowingSequence = true
            message = "Observa la secuencia..."
            delay(1000)
            for (index in sequence) {
                highlightedIndex = index
                delay(800)
                highlightedIndex = -1
                delay(400)
            }
            isShowingSequence = false
            message = "¡Tu turno!"
        }
    }

    fun onCellClick(index: Int, userId: Int) {
        if (isShowingSequence || isGameOver) return

        userSequence = userSequence + index
        
        val currentIndex = userSequence.size - 1
        if (userSequence[currentIndex] == sequence[currentIndex]) {
            hits++
        }

        if (userSequence.size == sequence.size) {
            calculatePrecision()
            isGameOver = true
            message = if (precision >= 80) "¡Excelente concentración!" else "Buen intento, necesitas descansar."
            saveSession(userId)
        }
    }

    private fun calculatePrecision() {
        precision = (hits.toFloat() / totalTouchesRequired.toFloat()) * 100
    }

    private fun saveSession(userId: Int) {
        viewModelScope.launch {
            val session = GameSession(
                idUser = userId,
                idJuego = 2, // ID para Memoria de Trabajo
                score = precision.toInt(),
                dateTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
            )
            repository.insertGameSession(session)
        }
    }
}
