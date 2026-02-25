package com.example.mindstack.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mindstack.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginView(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Configuración visual de los campos
    val textFieldColors = TextFieldDefaults.colors(
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp))
                .background(Color(0xFFCFDEE7)) // Azul pastel exacto
                .padding(horizontal = 40.dp, vertical = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Usuario
            Text(
                text = "Usuario:",
                modifier = Modifier.fillMaxWidth().padding(start = 12.dp, bottom = 4.dp),
                fontSize = 18.sp,
                color = Color.Black
            )
            TextField(
                value = username,
                onValueChange = { username = it },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(28.dp),
                colors = textFieldColors,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(25.dp))

            // Contraseña
            Text(
                text = "Contraseña:",
                modifier = Modifier.fillMaxWidth().padding(start = 12.dp, bottom = 4.dp),
                fontSize = 18.sp,
                color = Color.Black
            )
            TextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(28.dp),
                colors = textFieldColors,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(50.dp))

            Button(
                onClick = {
                    navController.navigate("main_view")
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

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}