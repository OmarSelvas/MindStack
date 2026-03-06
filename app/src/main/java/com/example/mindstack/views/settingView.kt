package com.example.mindstack.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
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
    var isEditing by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    if (user == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("No se ha iniciado sesión")
            Button(onClick = { navController.navigate("login_view") }) {
                Text("Ir al Login")
            }
        }
        return
    }

    var name by remember { mutableStateOf("${user.name} ${user.lastName}") }
    var email by remember { mutableStateOf(user.email) }
    var dob by remember { mutableStateOf(user.dateOfBirth) }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text(text = "Acerca de la App", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "Esta aplicación no reemplaza un diagnóstico médico profesional. " +
                            "Simplemente sirve como sugerencias para ayudarle a regular su sueño.",
                    fontSize = 16.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Entendido", color = Color(0xFFA294E3))
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFA294E3))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 48.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Perfil",
                fontSize = 32.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            Row {
                IconButton(onClick = { showAboutDialog = true }) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = "Acerca de", tint = Color.Black)
                }

                IconButton(onClick = {
                    // CORRECCIÓN: Se añade el bloque onSuccess exigido por el ViewModel
                    authViewModel.logout(onSuccess = {
                        navController.navigate("welcome") {
                            popUpTo(0)
                        }
                    })
                }) {
                    Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión", tint = Color.Black)
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier.size(150.dp).clip(CircleShape).background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.pinky_happy),
                        contentDescription = "Profile Picture",
                        modifier = Modifier.size(100.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { isEditing = !isEditing }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = name, color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp))
                .background(Color(0xFFF5F5F5))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfileDataField(label = "Nombre completo:", value = name)
            ProfileDataField(label = "Correo electrónico:", value = email)
            ProfileDataField(label = "Fecha de nacimiento:", value = dob)
            ProfileDataField(label = "Horas de sueño ideales:", value = "${user.idealSleepHours} hrs")
        }
    }
}

@Composable
fun ProfileDataField(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(start = 8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(Color.White)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(text = value, fontSize = 16.sp, color = Color.Black)
        }
    }
}