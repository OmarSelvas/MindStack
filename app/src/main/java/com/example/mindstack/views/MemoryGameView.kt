package com.example.mindstack.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mindstack.R
import com.example.mindstack.ui.AuthViewModel
import com.example.mindstack.viewmodels.MainViewModel
import com.example.mindstack.viewmodels.MemoryViewModel

@Composable
fun MemoryGameView(
    navController: NavController,
    authViewModel: AuthViewModel,
    mainViewModel: MainViewModel,
    viewModel: MemoryViewModel
) {
    // Obtenemos el ID de checkin real del dashboard
    val checkinId = mainViewModel.dashboardData?.todayCheckin?.checkinId 
                  ?: mainViewModel.dashboardData?.pendingCheckinId ?: 0

    // Reiniciar el juego al entrar para evitar estados de cuentas anteriores
    LaunchedEffect(Unit) {
        viewModel.resetGame()
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFD4E3ED))) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Memorama", fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 12.dp))

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Nivel: ${viewModel.currentLevel} / 3", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Text("Movimientos: ${viewModel.moves}", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(if (viewModel.currentLevel <= 2) 2 else 3),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(viewModel.cards) { index, card ->
                    val isFlipped = viewModel.flippedCards.contains(index) || viewModel.matchedCards.contains(index)

                    Card(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable(enabled = !isFlipped && !viewModel.isProcessing && !viewModel.isGameFinished) { 
                                viewModel.onCardClick(index, authViewModel, checkinId) 
                            },
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Image(
                                painter = painterResource(
                                    id = if (isFlipped) card.imageRes else R.drawable.carta_tapada
                                ),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize().padding(8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // VISTA DE VICTORIA
        AnimatedVisibility(
            visible = viewModel.isGameFinished,
            enter = fadeIn(animationSpec = tween(500)) + scaleIn(initialScale = 0.8f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .clickable { /* Bloquear clics al fondo */ },
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.padding(32.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.pinky_happy),
                            contentDescription = null,
                            modifier = Modifier.size(100.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("¡Felicidades!", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        Text("Has completado todos los niveles", fontSize = 16.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { 
                                // Refrescar dashboard para ver cambios
                                if (authViewModel.token.isNotEmpty()) {
                                    mainViewModel.fetchDashboard(authViewModel.token)
                                }
                                navController.popBackStack() 
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(25.dp)
                        ) {
                            Text("Finalizar", fontSize = 18.sp)
                        }
                    }
                }
            }
        }
    }
}
