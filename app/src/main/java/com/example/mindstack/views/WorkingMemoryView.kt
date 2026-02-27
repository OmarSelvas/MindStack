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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mindstack.ui.AuthViewModel
import com.example.mindstack.viewmodels.WorkingMemoryViewModel

@Composable
fun WorkingMemoryView(
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: WorkingMemoryViewModel = viewModel()
) {
    val sequence = viewModel.sequence
    val highlightedIndex = viewModel.highlightedIndex
    val isGameOver = viewModel.isGameOver
    val precision = viewModel.precision
    val message = viewModel.message
    val user = authViewModel.currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Memoria de Trabajo",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = message,
            fontSize = 18.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Cuadrícula 3x3
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            for (row in 0 until 3) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (col in 0 until 3) {
                        val index = row * 3 + col
                        val isHighlighted = highlightedIndex == index
                        
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .background(
                                    color = if (isHighlighted) Color(0xFFFFEB3B) else Color(0xFFE0E0E0),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    user?.let { viewModel.onCellClick(index, it.id) }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            // Celdas vacías, se iluminan por color
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        if (isGameOver) {
            Text(
                text = "Precisión: ${precision.toInt()}%",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (precision >= 80) Color(0xFF4CAF50) else Color(0xFFF44336)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { viewModel.startGame() },
                modifier = Modifier.fillMaxWidth(0.6f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A80B4))
            ) {
                Text("Reintentar")
            }
            
            TextButton(
                onClick = { navController.popBackStack() }
            ) {
                Text("Regresar a Juegos")
            }
        } else if (sequence.isEmpty()) {
            Button(
                onClick = { viewModel.startGame() },
                modifier = Modifier.fillMaxWidth(0.6f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A80B4))
            ) {
                Text("Empezar")
            }
        }
    }
}
