package com.example.mindstack.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mindstack.R
import com.example.mindstack.ui.AuthViewModel
import com.example.mindstack.viewmodels.HistoryItem
import com.example.mindstack.viewmodels.HistoryViewModel

@Composable
fun HistoryView(authViewModel: AuthViewModel, viewModel: HistoryViewModel = viewModel()) {
    val historyList = viewModel.historyList
    val isLoading = viewModel.isLoading
    var selectedItem by remember { mutableStateOf<HistoryItem?>(null) }

    LaunchedEffect(Unit) {
        if (authViewModel.token.isNotEmpty()) {
            viewModel.loadHistory(authViewModel.token)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F2E1))) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
            Spacer(modifier = Modifier.height(48.dp))
            Text("Historial", fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF4A80B4))
                }
            } else if (historyList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), Alignment.Center) {
                    Text("No hay registros aún", color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(20.dp), contentPadding = PaddingValues(bottom = 100.dp)) {
                    items(historyList) { item ->
                        HistoryCard(item) { selectedItem = item }
                    }
                }
            }
        }

        selectedItem?.let { item ->
            HistoryDetailDialog(item) { selectedItem = null }
        }
    }
}

@Composable
fun HistoryCard(item: HistoryItem, onVerMas: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(40.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text(text = item.displayDate, fontSize = 14.sp, color = Color.Gray)
                Image(painterResource(R.drawable.pinky_happy), null, Modifier.size(45.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(painterResource(if (item.battery >= 80) R.drawable.bateria_verde else R.drawable.bateria_amarilla), null, Modifier.size(60.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text("Nivel de batería: ${item.battery}%", fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(painterResource(item.trafficLightColor), null, Modifier.size(50.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text("Semáforo: ${item.trafficLightName}", fontSize = 15.sp)
            }
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomEnd) {
                Button(onClick = onVerMas, colors = ButtonDefaults.buttonColors(Color(0xFFF0D0E0)), shape = RoundedCornerShape(16.dp)) {
                    Text("Ver más...", color = Color.Black, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun HistoryDetailDialog(item: HistoryItem, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cerrar") } },
        title = { Text(item.displayDate, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("• Estado: ${item.mood}")
                Text("• Sueño: ${item.hoursSlept.toInt()} horas")
                Text("• Concentración: ${item.concentration}")
                Text("• Memoria: ${item.memory}")
                Text("• Batería: ${item.battery}%")
            }
        }
    )
}