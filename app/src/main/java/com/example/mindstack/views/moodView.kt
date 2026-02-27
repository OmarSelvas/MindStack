package com.example.mindstack.views

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.mindstack.R
import com.example.mindstack.data.AppDatabase
import com.example.mindstack.data.entities.DailyCheckin
import com.example.mindstack.data.repository.MindStackRepository
import com.example.mindstack.ui.AuthViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

data class Mood(val name: String, val color: Color, val imageRes: Int, val emoji: String)

val moods = listOf(
    Mood("Exhausto", Color(0xFF7D6353), R.drawable.pinky_exhausted, "😴"),
    Mood("Triste", Color(0xFFB09FD1), R.drawable.pinky_sad, "☹️"),
    Mood("Neutral", Color(0xFFF7E69C), R.drawable.pinky_neutral, "😐"),
    Mood("Feliz", Color(0xFF9EC086), R.drawable.pinky_happy, "😊"),
    Mood("Excelente", Color(0xFF99ACD6), R.drawable.pinky_excellent, "🤩")
)

@Composable
fun MoodView(navController: NavController, authViewModel: AuthViewModel) {
    var selectedMoodIndex by remember { mutableIntStateOf(2) }
    val currentMood = moods[selectedMoodIndex]
    val context = LocalContext.current
    val user = authViewModel.currentUser

    val repository = remember { 
        val dao = AppDatabase.getDatabase(context).mindStackDao()
        MindStackRepository(dao)
    }

    Scaffold(
        containerColor = Color(0xFFEFEEE0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            
            Text(
                text = "Indica tu estado de ánimo",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black,
                lineHeight = 36.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Me siento ${currentMood.name.lowercase()}",
                fontSize = 18.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(30.dp))

            Image(
                painter = painterResource(id = currentMood.imageRes),
                contentDescription = currentMood.name,
                modifier = Modifier.size(160.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Indicador "v v"
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("⌵", fontSize = 24.sp, color = Color.LightGray)
                Text("⌵", fontSize = 24.sp, color = Color.LightGray, modifier = Modifier.offset(y = (-15).dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Ruleta Arqueada Grande (Atravesando la pantalla)
            MoodArchedSlider(
                selectedMoodIndex = selectedMoodIndex,
                onMoodChange = { selectedMoodIndex = it }
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Botón para guardar el estado de ánimo
            Button(
                onClick = {
                    if (user != null) {
                        authViewModel.viewModelScope.launch {
                            val checkin = DailyCheckin(
                                idUser = user.id,
                                sleepStart = null,
                                sleepEnd = null,
                                hoursSleep = user.idealSleepHours, // Valor predeterminado
                                idMood = selectedMoodIndex + 1,
                                idStatus = 1,
                                dateTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()),
                                sleepDebt = 0f,
                                battery = 100
                            )
                            repository.insertDailyCheckin(checkin)
                            Toast.makeText(context, "Estado de ánimo guardado", Toast.LENGTH_SHORT).show()
                            navController.navigate("main_view")
                        }
                    } else {
                        Toast.makeText(context, "Inicia sesión primero", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A80B4))
            ) {
                Text("Confirmar Registro", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun MoodArchedSlider(selectedMoodIndex: Int, onMoodChange: (Int) -> Unit) {
    val totalAngle = 140f
    val startAngle = 270f - totalAngle / 2f
    val segmentAngle = totalAngle / moods.size
    
    val animatedAngle by animateFloatAsState(
        targetValue = startAngle + selectedMoodIndex * segmentAngle + segmentAngle / 2f,
        label = "needleAngle"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Canvas(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val canvasWidth = size.width.toFloat()
                    val canvasHeight = size.height.toFloat()
                    val outerRadius = canvasWidth * 0.9f
                    val center = Offset(canvasWidth / 2f, canvasHeight + 40.dp.toPx())
                    
                    val dx = offset.x - center.x
                    val dy = offset.y - center.y
                    var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                    if (angle < 0) angle += 360f
                    
                    if (angle in startAngle..(startAngle + totalAngle)) {
                        val newIndex = ((angle - startAngle) / segmentAngle).toInt().coerceIn(0, moods.size - 1)
                        onMoodChange(newIndex)
                    }
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    val canvasWidth = size.width.toFloat()
                    val canvasHeight = size.height.toFloat()
                    val outerRadius = canvasWidth * 0.9f
                    val center = Offset(canvasWidth / 2f, canvasHeight + 40.dp.toPx())
                    
                    val dx = change.position.x - center.x
                    val dy = change.position.y - center.y
                    var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                    if (angle < 0) angle += 360f
                    
                    if (angle in startAngle..(startAngle + totalAngle)) {
                        val newIndex = ((angle - startAngle) / segmentAngle).toInt().coerceIn(0, moods.size - 1)
                        onMoodChange(newIndex)
                    }
                }
            }
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val outerRadius = canvasWidth * 0.9f
            val center = Offset(canvasWidth / 2f, canvasHeight + 40.dp.toPx())
            val innerRadius = outerRadius * 0.75f
            val strokeWidth = outerRadius - innerRadius

            // Dibujar los segmentos del arco
            moods.forEachIndexed { index, mood ->
                drawArc(
                    color = mood.color,
                    startAngle = startAngle + index * segmentAngle,
                    sweepAngle = segmentAngle,
                    useCenter = false,
                    topLeft = Offset(center.x - outerRadius + strokeWidth/2, center.y - outerRadius + strokeWidth/2),
                    size = Size((outerRadius - strokeWidth/2) * 2, (outerRadius - strokeWidth/2) * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                )
                
                if (index > 0) {
                    val separatorAngle = Math.toRadians((startAngle + index * segmentAngle).toDouble())
                    val startSep = Offset(
                        center.x + innerRadius * cos(separatorAngle).toFloat(),
                        center.y + innerRadius * sin(separatorAngle).toFloat()
                    )
                    val endSep = Offset(
                        center.x + outerRadius * cos(separatorAngle).toFloat(),
                        center.y + outerRadius * sin(separatorAngle).toFloat()
                    )
                    drawLine(Color.White.copy(alpha = 0.5f), startSep, endSep, strokeWidth = 2f)
                }
            }

            // Dibujar la aguja / indicador
            this.rotate(degrees = animatedAngle + 90f, pivot = center) {
                drawCircle(
                    color = Color(0xFF3E2723),
                    radius = 12.dp.toPx(),
                    center = center
                )
                val path = Path().apply {
                    moveTo(center.x - 6.dp.toPx(), center.y)
                    lineTo(center.x + 6.dp.toPx(), center.y)
                    lineTo(center.x, center.y - outerRadius * 0.85f)
                    close()
                }
                drawPath(path, color = Color(0xFF3E2723))
            }
        }
    }
}
