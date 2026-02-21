package com.example.mindstack.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.mindstack.ui.theme.MindStackTheme

@Composable
fun WelcomeView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Bienvenido a MindStack!")
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeViewPreview() {
    MindStackTheme {
        WelcomeView()
    }
}
