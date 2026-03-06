package com.example.mindstack.views

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mindstack.R
import com.example.mindstack.ui.AuthViewModel
import com.example.mindstack.viewmodels.MemoryViewModel

@Composable
fun MemoryGameView(
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: MemoryViewModel
) {
    val checkinId = 1 // Ajustar según tu lógica de checkinId

    LaunchedEffect(Unit) {
        viewModel.startGame()
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFD4E3ED)).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Memorama", fontSize = 32.sp, modifier = Modifier.padding(vertical = 20.dp))

        Text("Movimientos: ${viewModel.moves}", fontSize = 20.sp)

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(viewModel.cards) { index, card ->
                val isFlipped = viewModel.flippedCards.contains(index) || viewModel.matchedCards.contains(index)

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .clickable { viewModel.onCardClick(index, authViewModel, checkinId) },
                    contentAlignment = Alignment.Center
                ) {
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

        if (viewModel.isGameFinished) {
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.padding(bottom = 40.dp)
            ) {
                Text("Finalizar")
            }
        }
    }
}