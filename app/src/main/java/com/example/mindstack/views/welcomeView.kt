package com.example.mindstack.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mindstack.R
import com.example.mindstack.ui.theme.MindStackTheme

@Composable
fun WelcomeView(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
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
        Spacer(modifier = Modifier.height(64.dp))
        TextButton(onClick = { navController.navigate("login_view") }) {
            Text("Iniciar sesión")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("register_view") }) {
            Text("Registrarse")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeViewPreview() {
    MindStackTheme {
        WelcomeView(navController = rememberNavController())
    }
}
