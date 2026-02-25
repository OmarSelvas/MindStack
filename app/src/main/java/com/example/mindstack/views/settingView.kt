package com.example.mindstack.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mindstack.R
import com.example.mindstack.ui.theme.MindStackTheme

@Composable
fun SettingView(navController: NavController) {
    var isEditing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("Ian Mauricio Morales Montejo") }
    var email by remember { mutableStateOf("correodeejemplo@gmail.com") }
    var username by remember { mutableStateOf("__Torao__") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFA294E3))
    ) {
        Text(
            text = "Perfil",
            fontSize = 32.sp,
            color = Color.Black,
            modifier = Modifier.padding(start = 24.dp, top = 48.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable(enabled = isEditing) {},
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.pinky_happy),
                        contentDescription = "Profile Picture",
                        modifier = Modifier.size(120.dp)
                    )
                    if (isEditing) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Cambiar foto", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE2D7F7))
                        .clickable { isEditing = !isEditing }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                        contentDescription = if (isEditing) "Save" else "Edit",
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            if (isEditing) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            } else {
                Text(text = name, color = Color.Black, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp))
                .background(Color(0xFFF5F5F5))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            EditableProfileField(label = "Correo:", value = email, isEditing = isEditing, onValueChange = { email = it })
            EditableProfileField(label = "Username", value = username, isEditing = isEditing, onValueChange = { username = it })
            ProfileField(label = "Cuenta creada:", value = "01/01/2024")
        }
    }
}

@Composable
fun EditableProfileField(label: String, value: String, isEditing: Boolean, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth(0.9f)) {
        Text(text = label, fontSize = 14.sp, color = Color.Black)
        if (isEditing) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFE5E4DE))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(text = value, fontSize = 14.sp, color = Color.Black)
            }
        }
    }
}

@Composable
fun ProfileField(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth(0.9f)) {
        Text(text = label, fontSize = 14.sp, color = Color.Black)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFE5E4DE))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(text = value, fontSize = 14.sp, color = Color.Black)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingViewPreview() {
    MindStackTheme {
        SettingView(navController = rememberNavController())
    }
}
