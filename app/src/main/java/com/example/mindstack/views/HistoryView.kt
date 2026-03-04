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
    val user = authViewModel.currentUser
    val historyList = viewModel.historyList
    val isLoading = viewModel.isLoading
    var selectedItem by remember { mutableStateOf<HistoryItem?>(null) }

    LaunchedEffect(user) {
        user?.let {
            viewModel.loadHistory(it.id, it.idealSleepHours)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F2E1)) // Color de fondo beige claro similar a la imagen
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                text = "Historial",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF4A80B4))
                }
            } else if (historyList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No hay registros aún", color = Color.Gray, fontSize = 18.sp)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(historyList) { item ->
                        HistoryCard(item) { selectedItem = item }
                    }
                }
            }
        }

        // Overlay de detalles (Dialog)
        selectedItem?.let { item ->
            HistoryDetailDialog(item) { selectedItem = null }
        }
    }
}

@Composable
fun HistoryCard(item: HistoryItem, onVerMas: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(40.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = item.displayDate, fontSize = 14.sp, color = Color.Gray)
                Image(
                    painter = painterResource(id = R.drawable.pinky_happy), // Usando el cerebro rosa
                    contentDescription = null,
                    modifier = Modifier.size(45.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = if (item.battery >= 80) R.drawable.bateria_verde else R.drawable.bateria_amarilla),
                    contentDescription = null,
                    modifier = Modifier.size(60.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Nivel de batería: ${item.battery}%", fontSize = 15.sp, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = item.trafficLightColor),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Semáforo: ${item.trafficLightName}", fontSize = 15.sp, color = Color.Black)
            }

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomEnd) {
                Button(
                    onClick = onVerMas,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0D0E0)),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(text = "Ver más...", color = Color.Black, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun HistoryDetailDialog(item: HistoryItem, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clickable(enabled = false) {},
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(text = item.displayDate, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                
                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        DetailText(label = "Estado de ánimo", value = item.mood)
                        DetailText(label = "Horas dormidas", value = "${item.hoursSlept.toInt()} horas")
                        DetailText(label = "Concentración", value = item.concentration)
                        DetailText(label = "Memoria", value = item.memory)
                    }
                    Image(
                        painter = painterResource(id = R.drawable.pinky_happy),
                        contentDescription = null,
                        modifier = Modifier.size(60.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = if (item.battery >= 80) R.drawable.bateria_verde else R.drawable.bateria_amarilla),
                        contentDescription = null,
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "${item.battery}%", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0D0E0)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(text = "Cerrar", color = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun DetailText(label: String, value: String) {
    Text(
        text = "• $label: $value",
        fontSize = 14.sp,
        color = Color.Black,
        modifier = Modifier.padding(vertical = 2.dp)
    )
}
