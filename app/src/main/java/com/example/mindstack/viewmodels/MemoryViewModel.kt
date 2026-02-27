package com.example.mindstack.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindstack.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class MemoryCard(
    val id: Int,
    val imageRes: Int,
    var isFlipped: Boolean = false,
    var isMatched: Boolean = false
)

class MemoryViewModel : ViewModel() {

    var currentLevel by mutableStateOf(1)
    var cards by mutableStateOf(listOf<MemoryCard>())
    var flippedCards by mutableStateOf(listOf<Int>())
    var moves by mutableStateOf(0)
    var timerSeconds by mutableStateOf(0)
    var isGameOver by mutableStateOf(false)
    var isLevelComplete by mutableStateOf(false)

    private var timerJob: kotlinx.coroutines.Job? = null

    val images = listOf(
        R.drawable.par_1, R.drawable.par_2, R.drawable.par_3,
        R.drawable.par_4, R.drawable.par_5, R.drawable.par_6
    )

    fun startGame(level: Int) {
        currentLevel = level
        isGameOver = false
        isLevelComplete = false
        moves = 0
        timerSeconds = 0
        flippedCards = emptyList()
        
        val pairCount = when (level) {
            1 -> 3
            2 -> 6 // 12 cartas (3x4)
            3 -> 8 // 16 cartas (4x4)
            else -> 3
        }

        // Seleccionar imágenes. Si necesitamos más de las que hay, repetimos algunas.
        val selectedImages = mutableListOf<Int>()
        for (i in 0 until pairCount) {
            selectedImages.add(images[i % images.size])
        }

        val cardList = (selectedImages + selectedImages).shuffled().mapIndexed { index, res ->
            MemoryCard(id = index, imageRes = res)
        }
        
        cards = cardList
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (!isGameOver && !isLevelComplete) {
                delay(1000)
                timerSeconds++
            }
        }
    }

    fun onCardClick(cardId: Int) {
        if (flippedCards.size >= 2 || cards[cardId].isFlipped || cards[cardId].isMatched) return

        val newCards = cards.toMutableList()
        newCards[cardId] = newCards[cardId].copy(isFlipped = true)
        cards = newCards
        
        flippedCards = flippedCards + cardId

        if (flippedCards.size == 2) {
            moves++
            viewModelScope.launch {
                delay(800)
                checkMatch()
            }
        }
    }

    private fun checkMatch() {
        if (flippedCards.size < 2) return
        val firstId = flippedCards[0]
        val secondId = flippedCards[1]
        val newCards = cards.toMutableList()

        if (cards[firstId].imageRes == cards[secondId].imageRes) {
            newCards[firstId] = newCards[firstId].copy(isMatched = true)
            newCards[secondId] = newCards[secondId].copy(isMatched = true)
        } else {
            newCards[firstId] = newCards[firstId].copy(isFlipped = false)
            newCards[secondId] = newCards[secondId].copy(isFlipped = false)
        }

        cards = newCards
        flippedCards = emptyList()

        if (cards.all { it.isMatched }) {
            isLevelComplete = true
            // Si quieres que termine el juego al completar el nivel 3
            if (currentLevel == 3) {
                isGameOver = true
            }
        }
    }

    fun nextLevel() {
        if (currentLevel < 3) {
            startGame(currentLevel + 1)
        } else {
            isGameOver = true
        }
    }

    fun formatTime(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return "%d:%02d".format(mins, secs)
    }
}
