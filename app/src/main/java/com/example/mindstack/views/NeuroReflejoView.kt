package com.example.mindstack.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mindstack.ui.AuthViewModel
import com.example.mindstack.viewmodels.NeuroReflejoViewModel

@Composable
fun NeuroReflejoView(
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: NeuroReflejoViewModel = viewModel()
) {
    val isGameRunning = viewModel.isGameRunning
    val isScreenGreen = viewModel.isScreenGreen
    val currentTrial = viewModel.currentTrial
    val isGameOver = viewModel.isGameOver
    val averageTime = viewModel.averageReactionTime
    val message = viewModel.message
    val user = authViewModel.currentUser

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isScreenGreen) Color(0xFF4CAF50) else Color.Black)
            .clickable(enabled = isGameRunning) {
                user?.let { viewModel.onScreenTouch(it.id) }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            if (!isGameRunning && !isGameOver) {
                Text(
                    text = "Neuro-Reflejo",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "Toca la pantalla lo más rápido posible cuando cambie a VERDE.",
                    fontSize = 18.sp,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
                Button(
                    onClick = { viewModel.startGame() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                    modifier = Modifier.height(56.dp).fillMaxWidth(0.6f)
                ) {
                    Text("Comenzar Test", fontSize = 18.sp)
                }
            } else if (isGameOver) {
                Text(
                    text = "¡Prueba Terminada!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Tu tiempo promedio fue:",
                    fontSize = 20.sp,
                    color = Color.LightGray
                )
                Text(
                    text = "${averageTime} ms",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = {
                        navController.navigate("main_view") {
                            popUpTo("main_view") { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                    modifier = Modifier.height(56.dp).fillMaxWidth(0.6f)
                ) {
                    Text("Aceptar", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = { viewModel.startGame() }
                ) {
                    Text("Reintentar", color = Color.White.copy(alpha = 0.7f), fontSize = 16.sp)
                }
            } else {
                // Durante el juego
                Text(
                    text = "Intento $currentTrial / 3",
                    fontSize = 20.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                Text(
                    text = message,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}