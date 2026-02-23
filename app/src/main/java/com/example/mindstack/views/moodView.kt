package com.example.mindstack.views

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mindstack.ui.theme.MindStackTheme
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import com.example.mindstack.R


data class Mood(
    val name: String,
    val color: Color,
    val emoji: String,
    val imageRes: Int )

val moods = listOf(
    Mood("Exhausto", Color(0xFFBDB2FF), "x_x", R.drawable.pinky_exhausted),
    Mood("Triste", Color(0xFFFFD6A5), ":(", R.drawable.pinky_sad),
    Mood("Neutral", Color(0xFFFDFFB6), ":|", R.drawable.pinky_neutral),
    Mood("Feliz", Color(0xFFCAFFBF), ":)", R.drawable.pinky_happy),
    Mood("Excelente", Color(0xFF9BF6FF), ":D", R.drawable.pinky_excellent)
)

@Composable
fun MoodView(navController: NavController) {
    var selectedMoodIndex by remember { mutableIntStateOf(2) }
    val currentMood = moods[selectedMoodIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5DC))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Button(onClick = { navController.popBackStack() }) {
            Text("Regresar")
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Indica tu estado de ánimo",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Me siento ${currentMood.name.lowercase()}",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
        }

        Image(
            painter = painterResource(id = R.drawable.pinky_excellent),
            contentDescription = currentMood.name,
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth(),
            alignment = Alignment.Center
        )

        MoodSlider(
            selectedMoodIndex = selectedMoodIndex,
            onMoodChange = { selectedMoodIndex = it }
        )
    }
}

@Composable
fun MoodSlider(selectedMoodIndex: Int, onMoodChange: (Int) -> Unit) {
    val segmentAngle = 180f / moods.size

    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .pointerInput(Unit) {
            detectDragGestures { change, _ ->
                val center = Offset(size.width / 2f, size.height.toFloat())
                val dragAngle = -atan2(
                    center.y - change.position.y,
                    center.x - change.position.x
                ) * (180f / Math.PI)

                if (dragAngle > 0 && dragAngle < 180) {
                    val newIndex = ((dragAngle / segmentAngle)).toInt().coerceIn(0, moods.size - 1)
                    onMoodChange(newIndex)
                }
            }
        }
    ) {
        val center = Offset(size.width / 2f, size.height)
        val radius = size.width / 2f

        // Dibuja los segmentos de color
        moods.forEachIndexed { index, mood ->
            drawArc(
                color = mood.color,
                startAngle = 180f + index * segmentAngle,
                sweepAngle = segmentAngle,
                useCenter = true,
                size = Size(radius * 2, radius * 2),
                topLeft = Offset(center.x - radius, center.y - radius)
            )
        }

        // Dibuja los emojis
        drawIntoCanvas { canvas ->
            val textPaint = Paint().asFrameworkPaint().apply {
                isAntiAlias = true
                textSize = 24.sp.toPx()
                color = Color.Black.toArgb()
                textAlign = android.graphics.Paint.Align.CENTER
            }
            moods.forEachIndexed { index, mood ->
                val angleRad = Math.toRadians((180 + index * segmentAngle + segmentAngle / 2).toDouble())
                val x = center.x + (radius * 0.7f) * cos(angleRad).toFloat()
                val y = center.y + (radius * 0.7f) * sin(angleRad).toFloat()
                canvas.nativeCanvas.drawText(mood.emoji, x, y, textPaint)
            }
        }

        // Dibuja el indicador
        val indicatorAngle = Math.toRadians((180 + selectedMoodIndex * segmentAngle + segmentAngle / 2).toDouble())
        val indicatorEndX = center.x + (radius * 0.9f) * cos(indicatorAngle).toFloat()
        val indicatorEndY = center.y + (radius * 0.9f) * sin(indicatorAngle).toFloat()

        drawCircle(color = Color(0xFF6B4F4F), radius = 20f, center = Offset(indicatorEndX, indicatorEndY))
        drawCircle(color = Color.White, radius = 8f, center = Offset(indicatorEndX, indicatorEndY))
    }
}

@Preview(showBackground = true)
@Composable
fun MoodViewPreview() {
    MindStackTheme {
        MoodView(navController = rememberNavController())
    }
}
