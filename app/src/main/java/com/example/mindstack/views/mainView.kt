package com.example.mindstack.views

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.* // Importa Arrangement y otros
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment // Importa Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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

    // Estados vinculados al CheckInViewModel
    val hasSleepStarted = checkInViewModel.savedSleepStart != null
    val moodSeleccionado = checkInViewModel.selectedMoodId != null
    val isMorning = LocalTime.now().hour < 12

    LaunchedEffect(checkInViewModel.checkInSuccess) {
        if (checkInViewModel.checkInSuccess) {
            Toast.makeText(context, "¡Datos sincronizados!", Toast.LENGTH_SHORT).show()
            checkInViewModel.checkInSuccess = false
        }
    }

    Scaffold(containerColor = Color.White) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Header
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp, vertical = 20.dp)) {
                Text("Home", fontSize = 36.sp, fontWeight = FontWeight.W500, color = Color.Black)
                Image(
                    painter = painterResource(id = R.drawable.pinky_happy),
                    contentDescription = null,
                    modifier = Modifier.size(130.dp).align(Alignment.TopEnd).offset(y = (-10).dp, x = 10.dp)
                )
            }

            // Contenedor principal
            Column(
                modifier = Modifier.fillMaxSize().padding(top = 130.dp)
                    .clip(RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp))
                    .background(Color(0xFFD4E3ED)).padding(horizontal = 24.dp).verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(30.dp))

                Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(16.dp)) {
                    CustomStatCard("Semáforo:", mainViewModel.trafficLight.colorName, mainViewModel.trafficLight.iconRes, Modifier.weight(1f))
                    CustomStatCard("Batería:", "${mainViewModel.cognitiveBattery}%", R.drawable.bateria_verde, Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(30.dp))

                if (checkInViewModel.isLoading) {
                    CircularProgressIndicator(color = Color(0xFF5589B7))
                } else {
                    Button(
                        onClick = {
                            if (!moodSeleccionado) {
                                navController.navigate("mood")
                            } else {
                                val now = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
                                val mood = checkInViewModel.selectedMoodId ?: 3

                                // Pasamos los argumentos según la firma del ViewModel
                                checkInViewModel.submitDailyCheckIn(
                                    isMorning = isMorning,
                                    currentTime = now,
                                    authViewModel = authViewModel,
                                    mood = mood
                                )
                            }
                        },
                        modifier = Modifier.width(240.dp).height(60.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!moodSeleccionado) Color.DarkGray else if (hasSleepStarted) Color(0xFF4CAF50) else Color(0xFF5589B7)
                        ),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text(if (!moodSeleccionado) "Indicar Humor" else if (hasSleepStarted) "¡Ya desperté!" else "A dormir", fontSize = 20.sp)
                    }
                }

                checkInViewModel.savedSleepStart?.let {
                    Text("Hora de inicio: $it", color = Color.DarkGray, modifier = Modifier.padding(top = 8.dp))
                }

                checkInViewModel.errorMessage?.let {
                    Text(it, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
                }

                Spacer(modifier = Modifier.height(120.dp))
            }
        }
    }
}

// --- El componente que te daba error ---
@Composable
fun CustomStatCard(label: String, value: String, iconRes: Int, modifier: Modifier) {
    Card(
        modifier = modifier.height(180.dp),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        // CORRECCIÓN: Asegúrate de que estos sean los parámetros exactos
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(value, fontSize = 16.sp, color = Color.Black)
            Image(painter = painterResource(id = iconRes), contentDescription = null, modifier = Modifier.size(65.dp))
        }
    }
}