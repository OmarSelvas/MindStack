package com.example.mindstack.views

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mindstack.ui.AuthViewModel
import java.util.Calendar

@Composable
fun RegisterView(navController: NavController, authViewModel: AuthViewModel) {
    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("M") }
    var localError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val mesFormateado = if (month + 1 < 10) "0${month + 1}" else "${month + 1}"
            val diaFormateado = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
            dob = "$year-$mesFormateado-$diaFormateado"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text("Crear Cuenta", fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(30.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Apellidos") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = dob,
            onValueChange = { },
            label = { Text("Fecha de Nacimiento") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { datePickerDialog.show() }) {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text("Género", modifier = Modifier.align(Alignment.Start), fontWeight = FontWeight.SemiBold)
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = gender == "M", onClick = { gender = "M" })
            Text("M")
            Spacer(modifier = Modifier.width(10.dp))
            RadioButton(selected = gender == "F", onClick = { gender = "F" })
            Text("F")
            Spacer(modifier = Modifier.width(10.dp))
            RadioButton(selected = gender == "O", onClick = { gender = "O" })
            Text("Otro")
        }

        Spacer(modifier = Modifier.height(30.dp))

        if (authViewModel.isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    if (password.length < 6) {
                        localError = "La contraseña debe tener al menos 6 caracteres"
                    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        localError = "Email inválido"
                    } else {
                        localError = null
                        authViewModel.registerUser(name, lastName, email, password, dob, gender) {
                            navController.navigate("main_view") {
                                popUpTo("register_view") { inclusive = true }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(55.dp)
            ) {
                Text("Registrarse")
            }
        }

        val displayError = localError ?: authViewModel.errorMessage
        displayError?.let {
            Text(it, color = Color.Red, modifier = Modifier.padding(top = 10.dp))
        }
    }
}
