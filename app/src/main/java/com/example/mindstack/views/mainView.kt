package com.example.mindstack.views

import android.widget.Toast
import androidx.compose.foundation.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mindstack.R
import com.example.mindstack.ui.AuthViewModel
import com.example.mindstack.ui.CheckInViewModel
import com.example.mindstack.viewmodels.MainViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun MainView(
    navController: NavController,
    authViewModel: AuthViewModel,
    checkInViewModel: CheckInViewModel
) {
    val mainViewModel: MainViewModel = viewModel()
    val context = LocalContext.current
    val data = mainViewModel.dashboardData
    val today = data?.todayCheckin

    // Estados reactivos del ViewModel de Check-in
    val hasSleepStarted = checkInViewModel.savedSleepStart != null
    val moodSeleccionado = checkInViewModel.selectedMoodId != null
    val isMorning = LocalTime.now().hour < 12

    // Carga inicial del Dashboard al entrar
    LaunchedEffect(Unit) {
        if (authViewModel.token.isNotEmpty()) {
            mainViewModel.fetchDashboard(authViewModel.token)
        }
    }

    // Refresco automático del Dashboard cuando el envío es exitoso
    LaunchedEffect(checkInViewModel.checkInSuccess) {
        if (checkInViewModel.checkInSuccess) {
            Toast.makeText(context, "¡Datos sincronizados!", Toast.LENGTH_SHORT).show()
            mainViewModel.fetchDashboard(authViewModel.token)
            checkInViewModel.checkInSuccess = false
        }
    }

    Scaffold(containerColor = Color.White) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            // --- HEADER CON RACHA ---
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp, vertical = 20.dp)) {
                Column {
                    Text("Home", fontSize = 36.sp, fontWeight = FontWeight.W500, color = Color.Black)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = if (data?.streak?.isActiveToday == true) R.drawable.racha else R.drawable.racha_gris),
                            contentDescription = null, modifier = Modifier.size(24.dp)
                        )
                        Text("${data?.streak?.currentStreak ?: 0} días jugados", Modifier.padding(start = 5.dp), fontSize = 14.sp)
                    }
                }
                Image(
                    painter = painterResource(id = R.drawable.pinky_happy),
                    contentDescription = null,
                    modifier = Modifier.size(130.dp).align(Alignment.TopEnd).offset(y = (-10).dp, x = 10.dp)
                )
            }

            // --- PANEL DE CONTENIDO AZUL ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 135.dp)
                    .clip(RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp))
                    .background(Color(0xFFD4E3ED))
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(25.dp))

                // CONSEJO DINÁMICO DE PINKY
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Text(
                        text = today?.semaphore?.recommendation ?: "¡Hola! Registra tu sueño para recibir un consejo.",
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // FILA 1: Semáforo y Batería
                Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(16.dp)) {
                    CustomStatCard(
                        "Semáforo:",
                        today?.semaphore?.label ?: "Cargando",
                        mainViewModel.getSemaphoreIcon(today?.semaphore?.color),
                        Modifier.weight(1f)
                    )
                    CustomStatCard(
                        "Batería:",
                        "${today?.batteryCog ?: 0}%",
                        mainViewModel.getBatteryIcon(today?.batteryCog ?: 0),
                        Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // FILA 2: Deuda y Horas Dormidas
                Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(16.dp)) {
                    CustomStatCard("Deuda:", "${today?.sleepDebt ?: 0.0} hrs", 0, Modifier.weight(1f))
                    CustomStatCard("Dormido:", "${today?.hoursSleep ?: 0.0} hrs", 0, Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(30.dp))

                // --- BOTÓN DE ACCIÓN RESTAURADO ---
                if (checkInViewModel.isLoading) {
                    CircularProgressIndicator(color = Color(0xFF5589B7))
                } else {
                    Button(
                        onClick = {
                            if (!moodSeleccionado) {
                                navController.navigate("mood")
                            } else {
                                val now = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
                                // LLAMADA DIRECTA: Pasamos el token String como lo pide el ViewModel
                                checkInViewModel.submitDailyCheckIn(
                                    isMorning = isMorning,
                                    currentTime = now,
                                    token = authViewModel.token,
                                    mood = checkInViewModel.selectedMoodId ?: 3
                                )
                            }
                        },
                        modifier = Modifier.width(240.dp).height(60.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!moodSeleccionado) Color.DarkGray
                            else if (hasSleepStarted) Color(0xFF4CAF50)
                            else Color(0xFF5589B7)
                        ),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text(
                            text = if (!moodSeleccionado) "Indicar Humor"
                            else if (hasSleepStarted) "¡Ya desperté!"
                            else "A dormir",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Info de estado debajo del botón
                checkInViewModel.savedSleepStart?.let {
                    Text("Hora inicio: $it", color = Color.DarkGray, modifier = Modifier.padding(top = 10.dp))
                }

                checkInViewModel.errorMessage?.let {
                    Text(it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun CustomStatCard(label: String, value: String, iconRes: Int, modifier: Modifier) {
    Card(
        modifier = modifier.height(160.dp),
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(label, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
            if (iconRes != 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Image(painter = painterResource(id = iconRes), contentDescription = null, modifier = Modifier.size(55.dp))
            }
        }
    }
}