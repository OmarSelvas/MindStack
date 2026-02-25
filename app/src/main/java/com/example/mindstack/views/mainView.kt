package com.example.mindstack.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mindstack.ui.theme.MindStackTheme

@Composable
fun MainView(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Main View")

        Button(onClick = { navController.navigate("mood") }) {
            Text("llevame a la encuesta")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainViewPreview() {
    MindStackTheme {
        MainView(navController = rememberNavController())
    }
}