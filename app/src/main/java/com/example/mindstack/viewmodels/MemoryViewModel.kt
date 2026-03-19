package com.example.mindstack.viewmodels

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindstack.R
import com.example.mindstack.data.RetrofitClient
import com.example.mindstack.data.network.MemoryGameRequest
import com.example.mindstack.ui.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MemoryViewModel : ViewModel() {
    var cards by mutableStateOf(listOf<MemoryCard>())
    var flippedCards = mutableStateListOf<Int>()
    var matchedCards = mutableStateListOf<Int>()
    var currentLevel by mutableStateOf(1)
    var moves by mutableStateOf(0)
    var isGameFinished by mutableStateOf(false)
    var isProcessing by mutableStateOf(false)

    private val allImages = listOf(
        R.drawable.par_1, R.drawable.par_2, R.drawable.par_3,
        R.drawable.par_4, R.drawable.par_5, R.drawable.par_6
    )

    fun resetGame() {
        currentLevel = 1
        isGameFinished = false
        startGame()
    }

    fun startGame() {
        val pairsCount = when(currentLevel) {
            1 -> 2
            2 -> 4
            else -> 6
        }
        val selectedImages = allImages.take(pairsCount)
        val shuffled = (selectedImages + selectedImages).shuffled()
        cards = shuffled.mapIndexed { index, res -> MemoryCard(index, res) }
        flippedCards.clear()
        matchedCards.clear()
        moves = 0
        isProcessing = false
    }

    fun onCardClick(index: Int, authVm: AuthViewModel, checkinId: Int) {
        if (isProcessing || flippedCards.contains(index) || matchedCards.contains(index) || isGameFinished) return
        
        flippedCards.add(index)
        if (flippedCards.size == 2) {
            moves++
            isProcessing = true
            checkMatch(authVm, checkinId)
        }
    }

    private fun checkMatch(authVm: AuthViewModel, checkinId: Int) {
        viewModelScope.launch {
            delay(500) // Reducido el tiempo de espera (era 800)
            if (cards[flippedCards[0]].imageRes == cards[flippedCards[1]].imageRes) {
                matchedCards.addAll(flippedCards)
                if (matchedCards.size == cards.size) {
                    if (currentLevel < 3) {
                        delay(500)
                        currentLevel++
                        startGame()
                    } else {
                        isGameFinished = true
                        submitResults(authVm, checkinId)
                    }
                }
            }
            flippedCards.clear()
            isProcessing = false
        }
    }

    fun submitResults(authVm: AuthViewModel, checkinId: Int) {
        viewModelScope.launch {
            try {
                val request = MemoryGameRequest(checkinId, matchedCards.size / 2, 6)
                RetrofitClient.gameService.submitMemoryGame("Bearer ${authVm.token}", request)
            } catch (e: Exception) {
                // Manejar error de red si es necesario
            }
        }
    }
}

data class MemoryCard(val id: Int, val imageRes: Int)
