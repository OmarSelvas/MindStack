package com.example.mindstack.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SurveyDialog(
    onDismiss: () -> Unit,
    onComplete: (List<String>) -> Unit
) {
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    val preguntas = listOf(
        "¿Consideras que el color del Semáforo de Riesgo mostrado hoy coincide con tu nivel real de cansancio?",
        "¿Has modificado o pausado tus actividades de estudio/trabajo basándote en el indicador de tu Batería Cognitiva?",
        "En una escala del 1 al 5, ¿qué tan precisos te parecen los resultados de los minijuegos para reflejar tu agilidad mental actual?",
        "Desde que usas la plataforma, ¿has intentado dormir más horas al notar una racha de 'Semáforo Rojo'?",
        "¿Sientes que gestionar tu energía con MindStack te ha ayudado a evitar episodios de agotamiento extremo?"
    )

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text("Encuesta MindStack", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        },
        text = {
            Column {
                LinearProgressIndicator(
                    progress = { (currentQuestionIndex + 1).toFloat() / preguntas.size },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    color = Color(0xFF5589B7)
                )
                Text(text = preguntas[currentQuestionIndex], fontSize = 16.sp)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (currentQuestionIndex < preguntas.size - 1) {
                        currentQuestionIndex++
                    } else {
                        onComplete(listOf()) // Aquí pasarías las respuestas recolectadas
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5589B7))
            ) {
                Text(if (currentQuestionIndex < preguntas.size - 1) "Siguiente" else "Finalizar")
            }
        },
        shape = RoundedCornerShape(28.dp),
        containerColor = Color.White
    )
}