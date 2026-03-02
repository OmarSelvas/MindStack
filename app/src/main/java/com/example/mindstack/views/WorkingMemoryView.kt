package com.example.mindstack.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mindstack.R
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

    // Colores de la paleta de la aplicación
    val fondoGrisClaro = Color(0xFFEFEEE0)
    val fondoPastel = Color(0xFFCFDEE7)
    val colorResaltado = Color(0xFFFFD54F) // Amarillo más suave estilo Pinky

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoGrisClaro),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Encabezado con Pinky (Estética coherente con minijuegos)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Text(
                text = "memoria",
                fontSize = 30.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                modifier = Modifier.align(Alignment.CenterStart)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Contenedor principal curvo
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
                .background(fondoPastel)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // Tarjeta de Mensaje/Estado
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                shape = RoundedCornerShape(25.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
            }

            // Cuadrícula 3x3 estilizada
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .shadow(8.dp, RoundedCornerShape(30.dp))
                    .background(Color.White, RoundedCornerShape(30.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    for (row in 0 until 3) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            for (col in 0 until 3) {
                                val index = row * 3 + col
                                val isHighlighted = highlightedIndex == index

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(15.dp))
                                        .background(
                                            if (isHighlighted) colorResaltado else Color(0xFFF0F0F0)
                                        )
                                        .clickable {
                                            user?.let { viewModel.onCellClick(index, it.id) }
                                        }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botones y Resultados
            if (isGameOver) {
                Text(
                    text = "Precisión: ${precision.toInt()}%",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = if (precision >= 80) Color(0xFF689F38) else Color(0xFFD32F2F)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.startGame() },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A80B4))
                ) {
                    Text("Reintentar", fontWeight = FontWeight.Bold)
                }

                TextButton(onClick = { navController.popBackStack() }) {
                    Text("Regresar", color = Color.DarkGray)
                }
            } else if (sequence.isEmpty()) {
                Button(
                    onClick = { viewModel.startGame() },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A80B4))
                ) {
                    Text("Comenzar Desafío", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
        }
    }
}