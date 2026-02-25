package com.example.mindstack.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mindstack.R
import com.example.mindstack.ui.theme.MindStackTheme
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterView(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var surnames by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf<LocalDate?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0E0E0)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.pinky_happy),
                contentDescription = "Mindstack Logo"
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Mindstack",
                style = MaterialTheme.typography.headlineLarge
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(Color(0xFFD0E0F0))
                .padding(32.dp)
        ) {
            Text(text = "Nombre(s):")
            OutlinedTextField(value = name, onValueChange = { name = it }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Apellidos:")
            OutlinedTextField(value = surnames, onValueChange = { surnames = it }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Fecha de nacimiento:")
            Button(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) {
                Text(text = dateOfBirth?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "Seleccionar fecha")
            }

            if (showDatePicker) {
                val datePickerState = rememberDatePickerState()
                val confirmEnabled = remember { derivedStateOf { datePickerState.selectedDateMillis != null } }

                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                datePickerState.selectedDateMillis?.let {
                                    dateOfBirth = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                                }
                                showDatePicker = false
                            },
                            enabled = confirmEnabled.value
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Cancelar")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Usuario:")
            OutlinedTextField(value = username, onValueChange = { username = it }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Correo:")
            OutlinedTextField(value = email, onValueChange = { email = it }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Contraseña:")
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    dateOfBirth?.let { dob ->
                        val age = Period.between(dob, LocalDate.now()).years
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarse")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterViewPreview() {
    MindStackTheme {
        RegisterView(navController = rememberNavController())
    }
}
