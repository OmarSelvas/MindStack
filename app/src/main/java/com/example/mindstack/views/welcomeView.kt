package com.example.mindstack.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mindstack.ui.theme.MindStackTheme

@Composable
fun WelcomeView(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Bienvenido a MindStack!")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("mood") }) {
            Text("Ir al cuestionario")
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
