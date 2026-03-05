package com.example.mindstack.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mindstack.ui.AuthViewModel

@Composable
fun RegisterView(navController: NavController, authViewModel: AuthViewModel) {
    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("M") }
    var idealSleep by remember { mutableStateOf("8.0") }

    LaunchedEffect(authViewModel.loginSuccess) {
        if (authViewModel.loginSuccess) {
            navController.navigate("main") {
                popUpTo("register") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD4E3ED))
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Crear Cuenta", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5589B7))

        Spacer(modifier = Modifier.height(20.dp))

        // Campos de texto (Resumidos para brevedad, asumiendo que tienes tus OutlinedTextFields)
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(20.dp))

        // CORRECCIÓN: Usar variables públicas del ViewModel
        if (authViewModel.isLoading) {
            CircularProgressIndicator(color = Color(0xFF5589B7))
        } else {
            Button(
                onClick = {
                    authViewModel.registerUser(
                        name = name,
                        lastName = lastName,
                        email = email,
                        pass = password,
                        dob = dob,
                        gender = gender,
                        idealSleep = idealSleep.toDoubleOrNull() ?: 8.0
                    )
                },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5589B7))
            ) {
                Text("Registrarme", fontSize = 18.sp, color = Color.White)
            }
        }

        // CORRECCIÓN: Mostrar error si existe
        authViewModel.errorMessage?.let { msg ->
            Text(text = msg, color = Color.Red, modifier = Modifier.padding(top = 10.dp))
        }
    }
}