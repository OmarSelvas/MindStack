package com.example.mindstack.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mindstack.ui.AuthViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterView(navController: NavController, authViewModel: AuthViewModel) {
    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var dob by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("M") }
    var acceptedPolicies by remember { mutableStateOf(false) }
    var showModal by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }
    
    var isPasswordFocused by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()

    // Password requirements logic
    val hasMinLength = password.length >= 8
    val hasUppercase = password.any { it.isUpperCase() }
    val hasLowercase = password.any { it.isLowerCase() }
    val hasDigit = password.any { it.isDigit() }
    val hasSpecialChar = password.any { !it.isLetterOrDigit() && "@#$%^&+=!$!%*?&#/._-".contains(it) }
    val isPasswordValid = hasMinLength && hasUppercase && hasLowercase && hasDigit && hasSpecialChar

    // MODAL DE TÉRMINOS Y CONDICIONES
    if (showModal) {
        AlertDialog(
            onDismissRequest = { showModal = false },
            title = { Text("Aviso de Privacidad y Términos", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "En cumplimiento con la Ley Federal de Protección de Datos Personales en Posesión de los Particulares (LFPDPPP), se informa que la aplicación *MindStack* recolecta datos personales y psicométricos con el único fin de gestionar la energía personal y el bienestar mental del usuario.\n\n" +
                                "- Se establece explícitamente que el tratamiento de estos datos tiene fines estrictamente académicos. Sus datos no serán compartidos con terceros bajo ninguna circunstancia.\n\n" +
                                "2. Derechos ARCO\nUsted tiene derecho a Acceder, Rectificar, Cancelar u Oponerse (Derechos ARCO) al tratamiento de su información.\nProcedimiento: Si desea que sus datos sean eliminados de nuestros registros, puede solicitarlo enviando un correo electrónico al administrador del equipo.\n\n" +
                                "3. Seguridad Técnica y Almacenamiento\nPara garantizar la integridad de su información, MindStack implementa los estándares internacionales de OWASP Mobile Top 10:\nCifrado de Datos: La base de datos local (Room) no se almacena en texto plano; se utiliza la librería SQLCipher para encriptar toda la información mediante una clave de seguridad.\nMínimo Privilegio: La aplicación solo solicita los permisos estrictamente necesarios para su funcionamiento (evitando el uso innecesario de GPS o Cámara).\n\n" +
                                "4. Deslinde de Responsabilidad Médica\nFuncionamiento del Semáforo: El indicador de bienestar se calcula comparando las horas de sueño reportadas por el usuario con su tiempo de reacción en las dinámicas de la app.\nNo Sustitución: Esta herramienta no sustituye el consejo médico profesional. MindStack es un gestor de bienestar y no debe utilizarse para diagnósticos clínicos.",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Justify,
                        color = Color.Black
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showModal = false }) {
                    Text("Entendido", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // NUEVO DATE PICKER MATERIAL 3 (CON MODO DE ENTRADA NUMÉRICA)
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
                        dob = date.format(DateTimeFormatter.ISO_LOCAL_DATE) // YYYY-MM-DD
                    }
                    showDatePicker = false
                }) {
                    Text("Seleccionar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = true, // Permite cambiar entre calendario y escritura numérica
                title = { Text("Fecha de nacimiento", modifier = Modifier.padding(16.dp)) }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text("Crear Cuenta", fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(30.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Apellidos") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = null)
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { isPasswordFocused = it.isFocused },
            singleLine = true
        )
        
        // Checklist con visibilidad animada al enfocar el campo
        AnimatedVisibility(
            visible = isPasswordFocused,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(start = 12.dp, top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                PasswordRequirementItem("Mínimo 8 caracteres", hasMinLength)
                PasswordRequirementItem("Una letra mayúscula", hasUppercase)
                PasswordRequirementItem("Una letra minúscula", hasLowercase)
                PasswordRequirementItem("Un número", hasDigit)
                PasswordRequirementItem("Un carácter especial (@#$%^&+=!)", hasSpecialChar)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = dob,
            onValueChange = { },
            label = { Text("Fecha de Nacimiento") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
            enabled = false, // Hacemos que toda la caja sea clickeable
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = Color.Black,
                disabledBorderColor = Color.Gray,
                disabledLabelColor = Color.Gray,
                disabledTrailingIconColor = Color.Gray
            ),
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text("Género", modifier = Modifier.align(Alignment.Start), fontWeight = FontWeight.SemiBold)
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = gender == "M", onClick = { gender = "M" })
            Text("M")
            Spacer(modifier = Modifier.width(10.dp))
            RadioButton(selected = gender == "F", onClick = { gender = "F" })
            Text("F")
            Spacer(modifier = Modifier.width(10.dp))
            RadioButton(selected = gender == "O", onClick = { gender = "O" })
            Text("Otro")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // CHECKBOX CON ENLACE A MODAL
        val annotatedText = buildAnnotatedString {
            append("Acepto las ")
            pushStringAnnotation(tag = "URL", annotation = "terms")
            withStyle(
                style = SpanStyle(
                    color = Color(0xFF4A80B4),
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append("políticas y términos de uso")
            }
            pop()
            append(" de MindStack.")
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = acceptedPolicies,
                onCheckedChange = { acceptedPolicies = it }
            )
            ClickableText(
                text = annotatedText,
                onClick = { offset ->
                    annotatedText.getStringAnnotations(tag = "URL", start = offset, end = offset)
                        .firstOrNull()?.let {
                            showModal = true
                        }
                },
                style = TextStyle(fontSize = 12.sp, color = Color.DarkGray, lineHeight = 16.sp)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        if (authViewModel.isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    if (!isPasswordValid) {
                        localError = "La contraseña no cumple con todos los requisitos"
                    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        localError = "Email inválido"
                    } else if (!acceptedPolicies) {
                        localError = "Debes aceptar las políticas para continuar"
                    } else {
                        localError = null
                        authViewModel.registerUser(name, lastName, email, password, dob, gender) {
                            navController.navigate("main_view") {
                                popUpTo("register_view") { inclusive = true }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                enabled = acceptedPolicies && isPasswordValid
            ) {
                Text("Registrarse")
            }
        }

        val displayError = localError ?: authViewModel.errorMessage
        displayError?.let {
            Text(it, color = Color.Red, modifier = Modifier.padding(top = 10.dp))
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun PasswordRequirementItem(text: String, isMet: Boolean) {
    val color by animateColorAsState(if (isMet) Color(0xFF4CAF50) else Color.Gray)
    val icon = if (isMet) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = color,
            fontSize = 12.sp,
            fontWeight = if (isMet) FontWeight.Bold else FontWeight.Normal
        )
    }
}
