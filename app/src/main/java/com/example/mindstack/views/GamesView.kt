package com.example.mindstack.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mindstack.R

@Composable
fun GamesView(navController: NavController) {
    // Fondo azul pastel suave basado en tus capturas
    val fondoPastel = Color(0xFFCFDEE7)
    val fondoGrisClaro = Color(0xFFEFEEE0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoGrisClaro), // Color base de la parte superior
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Encabezado con Pinky asomándose (como en la imagen del Home)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Text(
                text = "minijuegos",
                fontSize = 30.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                modifier = Modifier.align(Alignment.CenterStart)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Contenedor principal con curva pronunciada (estilo imagen Home)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
                .background(fondoPastel)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // Botón superior redondeado (estilo "Da click para jugar")
            Surface(
                color = Color.White.copy(alpha = 0.4f),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Text(
                    text = "Selecciona un reto",
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    fontSize = 18.sp,
                    color = Color.Black
                )
            }

            // Tarjetas de Juegos (Estilo Widgets Home)
            GameCard(
                title = "Memorama",
                subtitle = "Entrena tu memoria visual",
                iconRes = R.drawable.pinky_excellent // Usa iconos acordes
            ) {
                navController.navigate("memory_game")
            }

            Spacer(modifier = Modifier.height(20.dp))

            GameCard(
                title = "Memoria de Trabajo",
                subtitle = "Secuencia de luces (3x3)",
                iconRes = R.drawable.pinky_happy
            ) {
                navController.navigate("working_memory_game")
            }

            Spacer(modifier = Modifier.height(20.dp))

            GameCard(
                title = "Reacción",
                subtitle = "Prueba tus reflejos",
                iconRes = R.drawable.pinky_neutral
            ) {
                // Acción para juego de reacción
            }

            Spacer(modifier = Modifier.height(100.dp)) // Espacio para no chocar con el menú inferior
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameCard(title: String, subtitle: String, iconRes: Int, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp),
        shape = RoundedCornerShape(35.dp), // Bordes muy redondeados como en tus imágenes
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) // Sombra marcada para profundidad
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    lineHeight = 16.sp
                )
            }

            // Imagen del personaje a la derecha dentro de la tarjeta
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(65.dp)
            )
        }
    }
}