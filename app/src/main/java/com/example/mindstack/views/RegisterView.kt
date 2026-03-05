package com.example.mindstack.views

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mindstack.R
import com.example.mindstack.ui.AuthViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterView(navController: NavController, authViewModel: AuthViewModel) {
    var name by remember { mutableStateOf("") }
    var surnames by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Femenino") }
    var idealHours by remember { mutableStateOf("8.0") }

    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val selectedDate = datePickerState.selectedDateMillis?.let {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC")
        format.format(Date(it))
    } ?: ""

    LaunchedEffect(authViewModel.loginSuccess) {
        if (authViewModel.loginSuccess) {
            Toast.makeText(context, "¡Bienvenido/a!", Toast.LENGTH_SHORT).show()
            navController.navigate("main_view") { popUpTo(0) }
        }
    }

    val textFieldColors = TextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        cursorColor = Color.Black
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = { TextButton(onClick = { showDatePicker = false }) { Text("OK") } }
        ) { DatePicker(state = datePickerState) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD6D6D6))
            .imePadding() // Evita que el teclado tape los campos
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        Row(Modifier.fillMaxWidth(), Arrangement.Center, Alignment.CenterVertically) {
            Image(painterResource(R.drawable.pinky_happy), null, Modifier.size(60.dp))
            Spacer(Modifier.width(10.dp))
            Text("Mindstack", fontSize = 30.sp, color = Color.Black)
        }

        Spacer(modifier = Modifier.height(30.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp))
                .background(Color(0xFFCFDEE7))
                .padding(horizontal = 35.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            authViewModel.errorMessage?.let {
                Text(it, color = Color.Red, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
            }

            CustomLabel("Nombre(s):")
            TextField(name, { name = it }, Modifier.fillMaxWidth(), shape = RoundedCornerShape(25.dp), colors = textFieldColors)

            Spacer(Modifier.height(12.dp))
            CustomLabel("Apellidos:")
            TextField(surnames, { surnames = it }, Modifier.fillMaxWidth(), shape = RoundedCornerShape(25.dp), colors = textFieldColors)

            Spacer(Modifier.height(12.dp))
            CustomLabel("Género:")
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceEvenly) {
                listOf("Femenino", "Masculino", "Otro").forEach { option ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(gender == option, { gender = option })
                        Text(option, fontSize = 14.sp, color = Color.Black)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            CustomLabel("Horas de sueño ideales:")
            TextField(idealHours, { idealHours = it }, Modifier.fillMaxWidth(), shape = RoundedCornerShape(25.dp), colors = textFieldColors)

            Spacer(Modifier.height(12.dp))
            CustomLabel("Fecha de nacimiento:")
            Box(
                Modifier.fillMaxWidth().height(55.dp).clip(RoundedCornerShape(25.dp))
                    .background(Color.White).clickable { showDatePicker = true }.padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text(if(selectedDate.isEmpty()) "Seleccionar" else selectedDate, color = if(selectedDate.isEmpty()) Color.Gray else Color.Black)
                    Icon(Icons.Default.DateRange, null, tint = Color.Gray)
                }
            }

            Spacer(Modifier.height(12.dp))
            CustomLabel("Correo:")
            TextField(email, { email = it }, Modifier.fillMaxWidth(), shape = RoundedCornerShape(25.dp), colors = textFieldColors)

            Spacer(Modifier.height(12.dp))
            CustomLabel("Contraseña:")
            TextField(password, { password = it }, Modifier.fillMaxWidth(), shape = RoundedCornerShape(25.dp), colors = textFieldColors, visualTransformation = PasswordVisualTransformation())

            Spacer(Modifier.height(35.dp))

            if (authViewModel.isLoading) {
                CircularProgressIndicator(color = Color(0xFF4A80B4))
            } else {
                Button(
                    onClick = {
                        authViewModel.registerUser(
                            name, surnames, email, password, selectedDate, gender, idealHours.toDoubleOrNull() ?: 8.0
                        )
                    },
                    modifier = Modifier.width(220.dp).height(55.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A80B4))
                ) {
                    Text("Registrarse", color = Color.White, fontSize = 18.sp)
                }
            }
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

// --- FUNCIÓN CORREGIDA ---
@Composable
fun CustomLabel(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, bottom = 4.dp),
        fontSize = 15.sp, // Parámetro con nombre
        color = Color.Black, // Parámetro con nombre
        fontWeight = FontWeight.Medium // Parámetro con nombre
    )
}