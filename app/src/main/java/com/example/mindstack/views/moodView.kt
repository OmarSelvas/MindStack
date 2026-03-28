package com.example.mindstack.views

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.mindstack.ui.AuthViewModel
import com.example.mindstack.ui.CheckInViewModel
import androidx.compose.foundation.gestures.detectHorizontalDragGestures

// Datos de los estados de ánimo
data class MoodItem(val name: String, val color: Color, val imageRes: Int)

val listaMoods = listOf(
    MoodItem("Exhausto", Color(0xFFE9B86A), R.drawable.pinky_exhausted),
    MoodItem("Triste", Color(0xFFC0DCEA), R.drawable.pinky_sad),
    MoodItem("Neutral", Color(0xFF9CC173), R.drawable.pinky_neutral),
    MoodItem("Feliz", Color(0xFFB59BDD), R.drawable.pinky_happy),
    MoodItem("Excelente", Color(0xFF8BA6C9), R.drawable.pinky_excellent)
)

@Composable
fun MoodView(
    navController: NavController,
    authViewModel: AuthViewModel,
    checkInViewModel: CheckInViewModel
) {
    // Sincronización con el ViewModel (ID 1-5 mapeado a Index 0-4)
    var selectedMoodIndex by remember {
        mutableIntStateOf((checkInViewModel.selectedMoodId ?: 3) - 1)
    }
    val currentMood = listaMoods[selectedMoodIndex]
    val context = LocalContext.current

    Scaffold(containerColor = Color(0xFFEFEEE0)) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            Text("Indica tu estado de ánimo", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color.Black)
            Text("Me siento ${currentMood.name.lowercase()}", fontSize = 18.sp, color = Color.DarkGray)

            Spacer(modifier = Modifier.weight(0.5f))

            Image(
                painter = painterResource(id = currentMood.imageRes),
                contentDescription = null,
                modifier = Modifier.size(180.dp)
            )

            Spacer(modifier = Modifier.weight(0.5f))

            Text("▼", fontSize = 24.sp, color = Color(0xFF3E2723))

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
                    // LLAMADA CLAVE: Sincroniza con la función que agregamos al ViewModel
                    checkInViewModel.updateMood(selectedMoodIndex + 1)
                    Toast.makeText(context, "Estado de ánimo guardado", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(55.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A80B4))
            ) {
                Text("Confirmar Humor", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun MoodWheelCircularInfinita(selectedIndex: Int, onMoodChange: (Int) -> Unit) {
    val totalSegments = listaMoods.size
    val segmentAngle = 360f / totalSegments
    val baseOffset = 270f - (segmentAngle / 2f)

    val rotationAnim by animateFloatAsState(
        targetValue = baseOffset - (selectedIndex * segmentAngle),
        animationSpec = tween(durationMillis = 400),
        label = "wheelRotation"
    )

    var dragAmountAccumulated by remember { mutableFloatStateOf(0f) }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .pointerInput(selectedIndex) {
                detectHorizontalDragGestures(
                    onDragEnd = { dragAmountAccumulated = 0f },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        dragAmountAccumulated += dragAmount

                        val threshold = 70f
                        if (dragAmountAccumulated > threshold) {
                            val newIndex = if (selectedIndex > 0) selectedIndex - 1 else totalSegments - 1
                            onMoodChange(newIndex)
                            dragAmountAccumulated = 0f
                        } else if (dragAmountAccumulated < -threshold) {
                            val newIndex = if (selectedIndex < totalSegments - 1) selectedIndex + 1 else 0
                            onMoodChange(newIndex)
                            dragAmountAccumulated = 0f
                        }
                    }
                )
            }
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val centroRotacion = Offset(canvasWidth / 2f, canvasHeight * 1.15f)
        val radioVisual = canvasWidth * 0.8f

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
    }
}