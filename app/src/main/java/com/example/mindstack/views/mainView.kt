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
import java.util.Locale

@Composable
fun MainView(
    navController: NavController,
    authViewModel: AuthViewModel,
    checkInViewModel: CheckInViewModel,
    mainViewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    val data = mainViewModel.dashboardData
    val today = data?.todayCheckin

    // Usar el estado reactivo del ViewModel para la batería y semáforo
    val displayBattery = mainViewModel.currentBattery ?: data?.weekBatteryAvg?.toInt() ?: 0
    val semaphoreIcon = mainViewModel.getSemaphoreIcon(mainViewModel.currentSemaphoreColor, mainViewModel.currentSemaphoreLabel)
    val recommendationText = mainViewModel.currentRecommendation ?: "¡Hola! Registra tu sueño para recibir un consejo."

    val isSleeping = data?.hasPendingSleepStart == true || checkInViewModel.savedSleepStart != null
    val moodSeleccionado = checkInViewModel.selectedMoodId != null

    LaunchedEffect(Unit) {
        if (authViewModel.token.isNotEmpty()) {
            mainViewModel.fetchDashboard(authViewModel.token)
        }
    }

    LaunchedEffect(checkInViewModel.checkInSuccess) {
        if (checkInViewModel.checkInSuccess) {
            val msg = if (isSleeping) "¡Que descanses!" else "¡Buen día! Datos actualizados."
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            mainViewModel.fetchDashboard(authViewModel.token)
            checkInViewModel.checkInSuccess = false
        }
    }

    Scaffold(containerColor = Color.White) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

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

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Text(
                        text = recommendationText,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(16.dp)) {
                    CustomStatCard(
                        "Semáforo:",
                        mainViewModel.currentSemaphoreLabel ?: "---",
                        semaphoreIcon,
                        Modifier.weight(1f)
                    )
                    
                    CustomStatCard(
                        "Batería:",
                        "$displayBattery%",
                        mainViewModel.getBatteryIcon(displayBattery),
                        Modifier.weight(1f),
                        onClick = { navController.navigate("list") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                val sleepDebtFormatted = String.format(Locale.US, "%.1f", today?.sleepDebt ?: 0.0)
                val hoursSleepFormatted = String.format(Locale.US, "%.1f", today?.hoursSleep ?: 0.0)

                Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(16.dp)) {
                    CustomStatCard("Deuda:", "$sleepDebtFormatted hrs", 0, Modifier.weight(1f))
                    CustomStatCard("Dormido:", "$hoursSleepFormatted hrs", 0, Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(30.dp))

                if (mainViewModel.isLoading || checkInViewModel.isLoading) {
                    CircularProgressIndicator(color = Color(0xFF5589B7))
                } else {
                    Button(
                        onClick = {
                            val now = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
                            if (isSleeping) {
                                checkInViewModel.endSleep(now, authViewModel.token)
                            } else {
                                if (!moodSeleccionado) {
                                    navController.navigate("mood")
                                } else {
                                    checkInViewModel.startSleep(now)
                                }
                            }
                        },
                        modifier = Modifier.width(240.dp).height(60.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSleeping) Color(0xFF4CAF50) else Color(0xFF5589B7)
                        ),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text(
                            text = if (isSleeping) "¡Ya desperté!" else "A dormir",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (isSleeping) {
                    Text(
                        "Durmiendo desde: ${data?.hasPendingSleepStart ?: checkInViewModel.savedSleepStart}",
                        color = Color.DarkGray,
                        modifier = Modifier.padding(top = 10.dp)
                    )
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
fun CustomStatCard(label: String, value: String, iconRes: Int, modifier: Modifier, onClick: () -> Unit = {}) {
    Card(
        modifier = modifier.height(160.dp).clickable { onClick() },
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(label, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
            if (iconRes != 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Image(painter = painterResource(id = iconRes), contentDescription = null, modifier = Modifier.size(55.dp))
            }
        }
    }
}
