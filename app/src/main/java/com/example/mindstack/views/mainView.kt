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
import com.example.mindstack.components.SurveyDialog // Importación del nuevo componente
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun MainView(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    val mainViewModel: MainViewModel = viewModel()
    val user = authViewModel.currentUser

    // --- ESTADOS DE CONTROL ---
    var lastRecordedTime by remember { mutableStateOf("") }
    var showSurvey by remember { mutableStateOf(false) }

    // Supongamos que obtenemos la racha del ViewModel
    // Para probar el modal, puedes forzar este valor a 10 o 20
    val rachaActual = 10

    // --- LÓGICA DE DISPARO DEL MODAL ---
    LaunchedEffect(rachaActual) {
        if (rachaActual == 10 || rachaActual == 20) {
            showSurvey = true
        }
    }

    // --- LLAMADA AL COMPONENTE DE ENCUESTA ---
    if (showSurvey) {
        SurveyDialog(
            onDismiss = { showSurvey = false },
            onComplete = { respuestas ->
                // Aquí podrías enviar las respuestas al ViewModel
                showSurvey = false
            }
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
            // --- ENCABEZADO (Home y Pinky) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp, vertical = 20.dp)
            ) {
                Text(
                    text = "Home",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.W500,
                    color = Color.Black
                )
                Image(
                    painter = painterResource(id = R.drawable.pinky_happy),
                    contentDescription = "Pinky",
                    modifier = Modifier
                        .size(130.dp)
                        .align(Alignment.TopEnd)
                        .offset(y = (-10).dp, x = 10.dp)
                )
            }

            // --- PANEL AZUL CLARO (Fondo Curvo) ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 130.dp)
                    .clip(RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp))
                    .background(Color(0xFFD4E3ED))
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(30.dp))

                // --- SECCIÓN RACHA ---
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 20.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.racha),
                        contentDescription = "Racha",
                        modifier = Modifier.size(70.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$rachaActual días jugados",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                // --- MENSAJE / CONSEJO ---
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = mainViewModel.adviceMessage.ifEmpty { "Mensaje/consejo" },
                            fontSize = 22.sp,
                            textAlign = TextAlign.Center,
                            color = Color.DarkGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- FILA 1: SEMÁFORO Y BATERÍA ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CustomStatCard(
                        label = "Semáforo de riesgo:",
                        value = mainViewModel.trafficLight.colorName,
                        iconRes = mainViewModel.trafficLight.iconRes,
                        modifier = Modifier.weight(1f)
                    )
                    CustomStatCard(
                        label = "Batería cognitiva:",
                        value = "${mainViewModel.cognitiveBattery}%",
                        iconRes = if (mainViewModel.cognitiveBattery >= 80) R.drawable.bateria_verde else R.drawable.bateria_amarilla,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- FILA 2: DEUDA Y HORAS DORMIDAS ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CustomInfoCard(
                        label = "Deuda de sueño:",
                        value = "%.0f hrs".format(mainViewModel.sleepDebt),
                        modifier = Modifier.weight(1f)
                    )
                    CustomInfoCard(
                        label = "Horas dormidas:",
                        value = "6 hrs",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                // --- BOTÓN DINÁMICO ---
                val isMorning = LocalTime.now().hour < 12
                Button(
                    onClick = {
                        val now = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
                        lastRecordedTime = "Registrado: $now"
                    },
                    modifier = Modifier
                        .width(220.dp)
                        .height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5589B7)),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text(text = if (isMorning) "Levantarse" else "Dormir", fontSize = 22.sp)
                }

                if (lastRecordedTime.isNotEmpty()) {
                    Text(lastRecordedTime, modifier = Modifier.padding(top = 8.dp), color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(120.dp))
            }
        }
    }
}

// --- COMPONENTES AUXILIARES PARA TARJETAS ---

@Composable
fun CustomStatCard(label: String, value: String, iconRes: Int, modifier: Modifier) {
    Card(
        modifier = modifier.height(180.dp),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(label, fontSize = 15.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = Color.Black)
            Text(value, fontSize = 16.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(10.dp))
            Image(painter = painterResource(id = iconRes), contentDescription = null, modifier = Modifier.size(65.dp))
        }
    }
}

@Composable
fun CustomInfoCard(label: String, value: String, modifier: Modifier) {
    Card(
        modifier = modifier.height(180.dp),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(label, fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = Color.Black)
            Spacer(modifier = Modifier.height(10.dp))
            Text(value, fontSize = 38.sp, fontWeight = FontWeight.Light, color = Color.Black)
        }
    }
}