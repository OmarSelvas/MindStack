package com.example.mindstack.views

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mindstack.R
import com.example.mindstack.viewmodels.MemoryCard
import com.example.mindstack.viewmodels.MemoryViewModel

@Composable
fun MemoryGameView(navController: NavController, viewModel: MemoryViewModel = viewModel()) {
    
    var showStartDialog by remember { mutableStateOf(true) }

    // Dialogo inicial
    if (showStartDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("¿Listo para jugar?") },
            text = { Text("El memorama pondrá a prueba tu memoria visual a través de 3 niveles.") },
            confirmButton = {
                Button(onClick = { 
                    showStartDialog = false
                    viewModel.startGame(1) 
                }) {
                    Text("Comenzar")
                }
            },
            dismissButton = {
                TextButton(onClick = { navController.popBackStack() }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5E6D3)) 
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Memorama", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                Text(text = "Nivel ${viewModel.currentLevel}", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = viewModel.formatTime(viewModel.timerSeconds),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                color = Color.White.copy(alpha = 0.5f),
                shape = RoundedCornerShape(32.dp)
            ) {
                val columns = when (viewModel.currentLevel) {
                    1 -> 3
                    2 -> 3
                    3 -> 4
                    else -> 3
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(viewModel.cards) { card ->
                        MemoryCardItem(card) {
                            if (!showStartDialog) {
                                viewModel.onCardClick(card.id)
                            }
                        }
                    }
                }
            }
        }

        if (viewModel.isLevelComplete) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text(if (viewModel.currentLevel < 3) "¡Nivel Completado!" else "¡Felicidades!") },
                text = { Text("Lo lograste en ${viewModel.moves} movimientos y ${viewModel.formatTime(viewModel.timerSeconds)}.") },
                confirmButton = {
                    Button(onClick = { 
                        if (viewModel.currentLevel < 3) viewModel.nextLevel() else navController.popBackStack()
                    }) {
                        Text(if (viewModel.currentLevel < 3) "Siguiente Nivel" else "Finalizar")
                    }
                }
            )
        }
    }
}

@Composable
fun MemoryCardItem(card: MemoryCard, onClick: () -> Unit) {
    val rotation by animateFloatAsState(
        targetValue = if (card.isFlipped || card.isMatched) 180f else 0f,
        animationSpec = tween(durationMillis = 500)
    )

    Box(
        modifier = Modifier
            .aspectRatio(0.7f)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (rotation <= 90f) {
            Image(
                painter = painterResource(id = R.drawable.carta_tapada),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
        } else {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f },
                shape = RoundedCornerShape(8.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(2.dp, Color.LightGray)
            ) {
                Image(
                    painter = painterResource(id = card.imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}
