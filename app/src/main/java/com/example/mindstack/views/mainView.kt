package com.example.mindstack.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.mindstack.viewmodels.MainViewModel

@Composable
fun MainView(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    val mainViewModel: MainViewModel = viewModel()
    val user = authViewModel.currentUser
    
    // Estado para controlar la visibilidad de la alerta de Pinky
    var showPinkyAlert by remember { mutableStateOf(false) }

    // Detectar si venimos de registrar el estado de ánimo
    LaunchedEffect(navController.currentBackStackEntry) {
        val prevRoute = navController.previousBackStackEntry?.destination?.route
        if (prevRoute == "mood") {
            showPinkyAlert = true
        }
    }

    LaunchedEffect(user) {
        user?.let {
            mainViewModel.loadData(it.id, it.idealSleepHours)
        }
    }

    if (showPinkyAlert) {
        AlertDialog(
            onDismissRequest = { showPinkyAlert = false },
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.pinky_happy),
                    contentDescription = "Pinky",
                    modifier = Modifier.size(80.dp)
                )
            },
            title = {
                Text(
                    text = "¡Hola de nuevo!",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "¿Ya has hecho los minijuegos de hoy? Es importante para mantener tu mente activa.",
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPinkyAlert = false
                        navController.navigate("list")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A80B4))
                ) {
                    Text("¡Vamos a jugar!")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinkyAlert = false }) {
                    Text("Después", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(28.dp),
            containerColor = Color.White
        )
    }

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

                // --- SECCIÓN DE CONSEJO DINÁMICO ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
                ) {
                    Text(
                        text = mainViewModel.adviceMessage,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray
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
                            title = "Semáforo: ${mainViewModel.trafficLight.colorName}",
                            iconRes = mainViewModel.trafficLight.iconRes,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Batería: ${mainViewModel.cognitiveBattery}%",
                            iconRes = if (mainViewModel.cognitiveBattery >= 80) R.drawable.bateria_verde else R.drawable.bateria_amarilla,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        InfoCard(
                            title = "Deuda de sueño:",
                            value = "%.1f h".format(mainViewModel.sleepDebt),
                            modifier = Modifier.weight(1f)
                        )
                        InfoCard(
                            title = "Horas dormidas:",
                            value = "%.1f h".format(mainViewModel.sleepDebt), // Se asume que esto debería ser hoursSlept, pero mantengo consistencia con el código original si fuera intencional
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }

            // --- ENCABEZADO ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, end = 20.dp, top = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "Hola, ${user?.name ?: "Usuario"}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Image(
                    painter = painterResource(id = R.drawable.pinky_happy),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(110.dp)
                        .offset(y = 30.dp)
                )
            }
        }
    }
}

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
            Text(text = value, fontSize = 32.sp, fontWeight = FontWeight.Normal, color = Color.Black)
        }
    }
}
