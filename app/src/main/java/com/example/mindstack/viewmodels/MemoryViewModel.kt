package com.example.mindstack.viewmodels

import android.util.Log
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
    var isSending by mutableStateOf(false)

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
            delay(500)
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

    private fun submitResults(authVm: AuthViewModel, checkinId: Int) {
        if (checkinId == 0) {
            Log.e("MEMORY_SYNC", "No se puede enviar: checkinId es 0")
            return
        }

        viewModelScope.launch {
            isSending = true
            try {
                val token = if (authVm.token.startsWith("Bearer ")) authVm.token else "Bearer ${authVm.token}"
                // Enviamos correctHits (pares encontrados) y totalRequired
                val request = MemoryGameRequest(
                    idDailyCheckin = checkinId,
                    correctHits = matchedCards.size / 2,
                    totalRequired = 6 // Según tu lógica de niveles
                )
                
                Log.d("MEMORY_SYNC", "Enviando resultados: $request")
                val response = RetrofitClient.gameService.submitMemoryGame(token, request)
                
                if (response.isSuccessful) {
                    Log.d("MEMORY_SYNC", "Resultados guardados correctamente: ${response.body()}")
                } else {
                    Log.e("MEMORY_SYNC", "Error al guardar: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("MEMORY_SYNC", "Fallo de conexión: ${e.message}")
            } finally {
                isSending = false
            }
        }
    }
}

data class MemoryCard(val id: Int, val imageRes: Int)
