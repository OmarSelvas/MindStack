package com.example.mindstack.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.mindstack.ui.AuthViewModel

@Composable
fun SettingView(navController: NavController, authViewModel: AuthViewModel) {
    val user = authViewModel.currentUser
    var showAboutDialog by remember { mutableStateOf(false) }

    // Estado para controlar el movimiento del dedo
    val scrollState = rememberScrollState()

    if (user == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Sesión no iniciada", color = Color.Gray)
        }
        return
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("Información", fontWeight = FontWeight.Bold) },
            text = { Text("MindStack ayuda a regular tu sueño, pero no reemplaza un diagnóstico médico.") },
            confirmButton = { TextButton(onClick = { showAboutDialog = false }) { Text("Cerrar") } }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFA294E3))
    ) {
        // 1. CABECERA (Fija arriba)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Perfil", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Row {
                IconButton(onClick = { showAboutDialog = true }) {
                    Icon(Icons.Default.Info, null, tint = Color.Black)
                }
                IconButton(onClick = {
                    authViewModel.logout { navController.navigate("welcome") { popUpTo(0) } }
                }) {
                    Icon(Icons.Default.ExitToApp, null, tint = Color.Black)
                }
            }
        }

        // 2. CONTENEDOR BLANCO SCROLLEABLE
        // Usamos weight(1f) para que el scroll ocupe el resto de la pantalla sin cortarse
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp))
                .background(Color(0xFFF5F5F5))
                .verticalScroll(scrollState)
                .padding(horizontal = 32.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Avatar y Nombre
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.pinky_happy),
                        contentDescription = null,
                        modifier = Modifier.size(90.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "${user.name} ${user.lastName}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Datos del Usuario
            ProfileDataField(label = "Nombre Completo:", value = "${user.name} ${user.lastName}")
            Spacer(modifier = Modifier.height(16.dp))
            ProfileDataField(label = "Correo Electrónico:", value = user.email)
            Spacer(modifier = Modifier.height(16.dp))
            ProfileDataField(label = "Fecha de Nacimiento:", value = user.dateOfBirth)
            Spacer(modifier = Modifier.height(16.dp))
            ProfileDataField(label = "Objetivo de Sueño:", value = "${user.idealSleepHours} horas")

            // ESPACIADO FINAL CRÍTICO: Para que el scroll te deje ver el último campo
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun ProfileDataField(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(text = value, fontSize = 16.sp, color = Color.Black)
            }
        }
    }
}