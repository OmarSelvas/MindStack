    package com.example.mindstack.viewmodels
    
    import android.app.Application
    import android.util.Log
    import androidx.compose.runtime.*
    import androidx.lifecycle.AndroidViewModel
    import androidx.lifecycle.viewModelScope
    import com.example.mindstack.data.RetrofitClient
    // CORRECCIÓN: Importamos explícitamente desde network
    import com.example.mindstack.data.network.NeuroReflexRequest
    import com.example.mindstack.ui.AuthViewModel
    import kotlinx.coroutines.delay
    import kotlinx.coroutines.launch
    import kotlin.random.Random
    
    class NeuroReflejoViewModel(application: Application) : AndroidViewModel(application) {
        var isGameRunning by mutableStateOf(false)
        var isScreenGreen by mutableStateOf(false)
        var currentTrial by mutableStateOf(0)
        var reactionTimes = mutableListOf<Long>()
        var startTime by mutableStateOf(0L)
        var averageReactionTime by mutableStateOf(0L)
        var isGameOver by mutableStateOf(false)
        var isSending by mutableStateOf(false)
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
            message = "Espera al VERDE..."
            viewModelScope.launch {
                delay(Random.nextLong(2000, 5000))
                if (isGameRunning) {
                    isScreenGreen = true
                    startTime = System.currentTimeMillis()
                }
            }
        }
    
        fun onScreenTouch(authVm: AuthViewModel, checkinId: Int, onComplete: () -> Unit) {
            if (!isScreenGreen || isGameOver) return
            val time = System.currentTimeMillis() - startTime
            reactionTimes.add(time)
            isScreenGreen = false
            if (currentTrial < 3) {
                currentTrial++
                startNextTrial()
            } else {
                finishAndSubmit(authVm, checkinId, onComplete)
            }
        }
    
        private fun finishAndSubmit(authVm: AuthViewModel, checkinId: Int, onComplete: () -> Unit) {
            isGameOver = true
            isGameRunning = false
            averageReactionTime = reactionTimes.average().toLong()
    
            viewModelScope.launch {
                isSending = true
                try {
                    // Aquí ya reconoce NeuroReflexRequest gracias al import de network
                    val request = NeuroReflexRequest(
                        idDailyCheckin = checkinId,
                        reactionTime1Ms = reactionTimes[0].toDouble(),
                        reactionTime2Ms = reactionTimes[1].toDouble(),
                        reactionTime3Ms = reactionTimes[2].toDouble()
                    )
                    RetrofitClient.gameService.submitNeuroReflex("Bearer ${authVm.token}", request)
                } catch (e: Exception) {
                    Log.e("NEURO_ERROR", e.message ?: "Error")
                } finally {
                    isSending = false
                    onComplete()
                }
            }
        }
    }