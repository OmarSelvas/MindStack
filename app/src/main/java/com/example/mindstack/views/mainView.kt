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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mindstack.R

@Composable
fun MainView(navController: NavController) {
    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- PANEL AZUL INFERIOR ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 100.dp)
                    .clip(RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp))
                    .background(Color(0xFFCFDEE7))
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(60.dp))

                // --- SECCIÓN DE RACHA (FUEGO + TEXTO) ---
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.racha), // Asegúrate de tener el ícono de racha
                        contentDescription = "Racha",
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "25 días jugados",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Cuadrícula de tarjetas
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StatCard(
                            title = "Semáforo de riesgo: Verde",
                            iconRes = R.drawable.semaforo_verde,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Batería cognitiva: 100%",
                            iconRes = R.drawable.bateria_verde,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        InfoCard(
                            title = "Deuda de sueño:",
                            value = "2 hrs",
                            modifier = Modifier.weight(1f)
                        )
                        InfoCard(
                            title = "Horas dormidas:",
                            value = "6 hrs",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }

            // --- ENCABEZADO (Home y Pinky Flotante) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, end = 20.dp, top = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "Home",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                )
                Image(
                    painter = painterResource(id = R.drawable.pinky_happy),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(130.dp)
                        .offset(y = 40.dp)
                )
            }
        }
    }
}

// Componentes StatCard e InfoCard se mantienen igual que en el código anterior...
@Composable
fun StatCard(title: String, iconRes: Int, modifier: Modifier) {
    Card(
        modifier = modifier.height(160.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = title, textAlign = TextAlign.Center, fontSize = 14.sp, color = Color.Black, lineHeight = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Image(painter = painterResource(id = iconRes), contentDescription = null, modifier = Modifier.size(55.dp))
        }
    }
}

@Composable
fun InfoCard(title: String, value: String, modifier: Modifier) {
    Card(
        modifier = modifier.height(160.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 36.sp, fontWeight = FontWeight.Normal, color = Color.Black)
        }
    }
}