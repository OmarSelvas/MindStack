package com.example.mindstack.views

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mindstack.R
import com.example.mindstack.data.AppDatabase
import com.example.mindstack.data.entities.DailyCheckin
import com.example.mindstack.data.repository.MindStackRepository
import com.example.mindstack.ui.AuthViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class MoodItem(val name: String, val color: Color, val imageRes: Int)

val listaMoods = listOf(
    MoodItem("Exhausto", Color(0xFFE9B86A), R.drawable.pinky_exhausted),
    MoodItem("Triste", Color(0xFFC0DCEA), R.drawable.pinky_sad),
    MoodItem("Neutral", Color(0xFF9CC173), R.drawable.pinky_neutral),
    MoodItem("Feliz", Color(0xFFB59BDD), R.drawable.pinky_happy),
    MoodItem("Excelente", Color(0xFF8BA6C9), R.drawable.pinky_excellent)
)

@Composable
fun MoodView(navController: NavController, authViewModel: AuthViewModel) {
    var selectedMoodIndex by remember { mutableIntStateOf(2) }
    val currentMood = listaMoods[selectedMoodIndex]
    val context = LocalContext.current
    val user = authViewModel.currentUser
    val coroutineScope = rememberCoroutineScope()

    Scaffold(containerColor = Color(0xFFEFEEE0)) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            Text("Indica tu estado de ánimo", fontSize = 28.sp, fontWeight = FontWeight.Black)
            Text("Me siento ${currentMood.name.lowercase()}", fontSize = 18.sp, color = Color.DarkGray)

            Spacer(modifier = Modifier.weight(0.5f))

            Image(
                painter = painterResource(id = currentMood.imageRes),
                contentDescription = null,
                modifier = Modifier.size(140.dp)
            )

            Spacer(modifier = Modifier.weight(0.5f))

            Text("▼", fontSize = 20.sp, color = Color(0xFF3E2723))

            // Dimensiones de la versión anterior (180.dp de altura)
            Box(
                modifier = Modifier.fillMaxWidth().height(180.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                MoodWheelCircularInfinita(
                    selectedIndex = selectedMoodIndex,
                    onMoodChange = { selectedMoodIndex = it }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    user?.let { u ->
                        coroutineScope.launch {
                            try {
                                val dao = AppDatabase.getDatabase(context).mindStackDao()
                                val repository = MindStackRepository(dao)
                                val checkin = DailyCheckin(
                                    idUser = u.id,
                                    sleepStart = null, sleepEnd = null,
                                    hoursSleep = 8.0f,
                                    idMood = selectedMoodIndex + 1,
                                    idStatus = 1,
                                    dateTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()),
                                    sleepDebt = 0f, battery = 100
                                )
                                repository.insertDailyCheckin(checkin)
                                Toast.makeText(context, "Guardado", Toast.LENGTH_SHORT).show()
                                navController.navigate("main_view")
                            } catch (e: Exception) { }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A80B4))
            ) {
                Text("Confirmar Registro", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun MoodWheelCircularInfinita(selectedIndex: Int, onMoodChange: (Int) -> Unit) {
    val totalSegments = listaMoods.size
    val segmentAngle = 360f / totalSegments

    // Estado para acumular la rotación y evitar que "rebote" al dar la vuelta
    var rotationOffset by remember { mutableFloatStateOf(0f) }
    var accumulatedDrag by remember { mutableFloatStateOf(0f) }

    // Sincronizar el offset con el índice de forma infinita
    LaunchedEffect(selectedIndex) {
        val targetRotation = -(selectedIndex * segmentAngle)
        // Lógica para encontrar el camino más corto en el círculo
        var diff = (targetRotation - rotationOffset) % 360f
        if (diff > 180f) diff -= 360f
        if (diff < -180f) diff += 360f
        rotationOffset += diff
    }

    val rotationAnim by animateFloatAsState(
        targetValue = rotationOffset + 270f - (segmentAngle / 2f),
        animationSpec = tween(400),
        label = "wheelRotation"
    )

    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(180.dp) // Misma dimensión que la anterior
        .pointerInput(selectedIndex) {
            detectDragGestures(
                onDrag = { change, dragAmount ->
                    change.consume()
                    accumulatedDrag += dragAmount.x

                    if (accumulatedDrag > 60f) {
                        val prev = if (selectedIndex > 0) selectedIndex - 1 else totalSegments - 1
                        onMoodChange(prev)
                        accumulatedDrag = 0f
                    } else if (accumulatedDrag < -60f) {
                        val next = if (selectedIndex < totalSegments - 1) selectedIndex + 1 else 0
                        onMoodChange(next)
                        accumulatedDrag = 0f
                    }
                },
                onDragEnd = { accumulatedDrag = 0f }
            )
        }
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Centro de rotación ajustado para que se vea circular pero asome poco (como la anterior)
        val centroRotacion = Offset(canvasWidth / 2f, canvasHeight * 1.1f)
        val radioVisual = canvasWidth * 0.75f

        rotate(degrees = rotationAnim, pivot = centroRotacion) {
            listaMoods.forEachIndexed { index, mood ->
                drawArc(
                    color = mood.color,
                    startAngle = index * segmentAngle,
                    sweepAngle = segmentAngle,
                    useCenter = true,
                    topLeft = Offset(centroRotacion.x - radioVisual, centroRotacion.y - radioVisual),
                    size = Size(radioVisual * 2, radioVisual * 2),
                    style = Fill
                )
            }
        }

        // AGUJA MARRÓN ESTÁTICA (Justo donde estaba en la anterior)
        drawCircle(
            color = Color(0xFF3E2723),
            radius = 15.dp.toPx(),
            center = Offset(canvasWidth / 2f, canvasHeight * 0.75f)
        )
    }
}