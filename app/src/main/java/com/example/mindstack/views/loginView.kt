package com.example.mindstack.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mindstack.R
import com.example.mindstack.ui.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginView(navController: NavController, authViewModel: AuthViewModel) {
    // Variables de estado
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var otpCode by remember { mutableStateOf("") } // Nueva variable para el código
    val context = LocalContext.current

    // Observar el éxito del login para navegar al Home
    LaunchedEffect(authViewModel.loginSuccess) {
        if (authViewModel.loginSuccess) {
            navController.navigate("main_view") {
                popUpTo("login_view") { inclusive = true }
            }
        }
    }

    // Estilos de los inputs
    val textFieldColors = TextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        disabledContainerColor = Color.White,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        cursorColor = Color.Black
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD6D6D6)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Cabecera con Logo
        Spacer(modifier = Modifier.height(80.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.pinky_happy),
                contentDescription = "Mindstack Logo",
                modifier = Modifier.size(70.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Mindstack",
                fontSize = 32.sp,
                color = Color.Black,
                fontWeight = FontWeight.Normal
            )
        }

        Spacer(modifier = Modifier.height(60.dp))

        // Contenedor principal del formulario
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp))
                .background(Color(0xFFCFDEE7))
                .padding(horizontal = 40.dp, vertical = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Mostrar errores del ViewModel
            authViewModel.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 16.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }

            // Alternar entre Login y OTP
            if (!authViewModel.isWaitingForOtp) {
                // ===============================================
                // PANTALLA 1: FORMULARIO NORMAL DE LOGIN
                // ===============================================
                Text(
                    text = "Correo:",
                    modifier = Modifier.fillMaxWidth().padding(start = 12.dp, bottom = 4.dp),
                    fontSize = 18.sp,
                    color = Color.Black
                )
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = textFieldColors,
                    singleLine = true,
                    placeholder = { Text("ejemplo@correo.com", color = Color.Gray) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(25.dp))

                Text(
                    text = "Contraseña:",
                    modifier = Modifier.fillMaxWidth().padding(start = 12.dp, bottom = 4.dp),
                    fontSize = 18.sp,
                    color = Color.Black
                )
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    shape = RoundedCornerShape(28.dp),
                    colors = textFieldColors,
                    singleLine = true,
                    trailingIcon = {
                        val image = if (passwordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña")
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    )
                )

                Spacer(modifier = Modifier.height(50.dp))

                if (authViewModel.isLoading) {
                    CircularProgressIndicator(
                        color = Color(0xFF4A80B4),
                        modifier = Modifier.size(50.dp)
                    )
                } else {
                    Button(
                        onClick = {
                            // Se llama al login sin el onSuccess
                            authViewModel.login(email, password)
                        },
                        modifier = Modifier
                            .width(220.dp)
                            .height(55.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Text(text = "Iniciar sesión", color = Color.Black, fontSize = 18.sp)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                TextButton(onClick = { navController.navigate("register_view") }) {
                    Text("¿No tienes cuenta? Regístrate", color = Color.Black)
                }

            } else {
                // ===============================================
                // PANTALLA 2: INGRESO DE CÓDIGO OTP
                // ===============================================
                Text(
                    text = "Verificación en 2 pasos",
                    fontSize = 22.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ingresa el código enviado a:",
                    color = Color.DarkGray,
                    fontSize = 16.sp
                )
                Text(
                    text = authViewModel.temporaryEmail,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    text = "Código de 6 dígitos:",
                    modifier = Modifier.fillMaxWidth().padding(start = 12.dp, bottom = 4.dp),
                    fontSize = 18.sp,
                    color = Color.Black
                )
                TextField(
                    value = otpCode,
                    onValueChange = { if (it.length <= 6) otpCode = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = textFieldColors,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    )
                )

                Spacer(modifier = Modifier.height(50.dp))

                if (authViewModel.isLoading) {
                    CircularProgressIndicator(
                        color = Color(0xFF4A80B4),
                        modifier = Modifier.size(50.dp)
                    )
                } else {
                    Button(
                        onClick = {
                            authViewModel.verifyOtp(otpCode) {
                                // Navegación manual por si el LaunchedEffect se retrasa
                                navController.navigate("main_view") {
                                    popUpTo("login_view") { inclusive = true }
                                }
                            }
                        },
                        modifier = Modifier
                            .width(220.dp)
                            .height(55.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                        enabled = otpCode.length == 6
                    ) {
                        Text(text = "Confirmar", color = Color.Black, fontSize = 18.sp)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                TextButton(onClick = { authViewModel.cancelOtp() }) {
                    Text("Cancelar y regresar", color = Color.Black)
                }
            }
        }
    }
}