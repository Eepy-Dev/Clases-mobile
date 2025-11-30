package com.example.appmovil.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmovil.R
import com.example.appmovil.ui.theme.ChocolateDark
import com.example.appmovil.ui.theme.ChocolateLight
import com.example.appmovil.ui.theme.Cream
import com.example.appmovil.ui.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    loginViewModel: LoginViewModel
) {
    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var loginResult by remember { mutableStateOf(false) }
    var erroresValidacion by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    
    DisposableEffect(Unit) {
        val loginObserver = androidx.lifecycle.Observer<Boolean> { result ->
            loginResult = result
            if (result) {
                onLoginSuccess()
            }
        }
        loginViewModel.loginResult.observeForever(loginObserver)
        onDispose {
            loginViewModel.loginResult.removeObserver(loginObserver)
        }
    }
    
    LaunchedEffect(loginResult) {
        if (loginResult) {
            onLoginSuccess()
        }
    }
    
    // Animaciones
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo con animacion de fade in y scale
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(
                    animationSpec = tween(600, delayMillis = 100)
                ) + scaleIn(
                    initialScale = 0.8f,
                    animationSpec = tween(600, delayMillis = 100)
                ),
                exit = ExitTransition.None
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logos),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .width(254.dp)
                        .height(217.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Título de bienvenida con animacion de slide
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(400, delayMillis = 300)
                ) + fadeIn(
                    animationSpec = tween(400, delayMillis = 300)
                ),
                exit = ExitTransition.None
            ) {
                Text(
                    text = "Bienvenidos!",
                    color = ChocolateDark,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(11.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Campos con animacion secuencial
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(400, delayMillis = 500)
                ) + fadeIn(
                    animationSpec = tween(400, delayMillis = 500)
                ),
                exit = ExitTransition.None
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Campo de usuario
                    Column(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = usuario,
                            onValueChange = { 
                                usuario = it
                                // Limpiar error cuando el usuario empieza a escribir
                                erroresValidacion = erroresValidacion.filterKeys { it != "usuario" && it != "general" }
                            },
                            label = { Text("Usuario") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            isError = erroresValidacion.containsKey("usuario") || erroresValidacion.containsKey("general"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (erroresValidacion.containsKey("usuario") || erroresValidacion.containsKey("general")) Color.Red else ChocolateLight,
                                unfocusedBorderColor = if (erroresValidacion.containsKey("usuario") || erroresValidacion.containsKey("general")) Color.Red else ChocolateLight,
                                errorBorderColor = Color.Red,
                                focusedLabelColor = ChocolateDark,
                                unfocusedLabelColor = ChocolateDark,
                                errorLabelColor = Color.Red,
                                focusedTextColor = ChocolateDark,
                                unfocusedTextColor = ChocolateDark
                            )
                        )
                        // Mensaje de error debajo del campo
                        erroresValidacion["usuario"]?.let { error ->
                            Text(
                                text = error,
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )
                        }
                    }
                    
                    // Campo de contraseña
                    Column(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = contrasena,
                            onValueChange = { 
                                contrasena = it
                                // Limpiar error cuando el usuario empieza a escribir
                                erroresValidacion = erroresValidacion.filterKeys { it != "contrasena" && it != "general" }
                            },
                            label = { Text("Contraseña") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            isError = erroresValidacion.containsKey("contrasena") || erroresValidacion.containsKey("general"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (erroresValidacion.containsKey("contrasena") || erroresValidacion.containsKey("general")) Color.Red else ChocolateLight,
                                unfocusedBorderColor = if (erroresValidacion.containsKey("contrasena") || erroresValidacion.containsKey("general")) Color.Red else ChocolateLight,
                                errorBorderColor = Color.Red,
                                focusedLabelColor = ChocolateDark,
                                unfocusedLabelColor = ChocolateDark,
                                errorLabelColor = Color.Red,
                                focusedTextColor = ChocolateDark,
                                unfocusedTextColor = ChocolateDark
                            )
                        )
                        // Mensaje de error debajo del campo
                        erroresValidacion["contrasena"]?.let { error ->
                            Text(
                                text = error,
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )
                        }
                        // Error general (credenciales incorrectas) debajo de contraseña
                        erroresValidacion["general"]?.let { error ->
                            Text(
                                text = error,
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Botón de login con animacion
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(
                    animationSpec = tween(400, delayMillis = 700)
                ) + scaleIn(
                    initialScale = 0.9f,
                    animationSpec = tween(400, delayMillis = 700)
                ),
                exit = ExitTransition.None
            ) {
                Button(
                    onClick = {
                        val resultado = loginViewModel.validarLogin(usuario.trim(), contrasena.trim())
                        erroresValidacion = resultado.errores
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = com.example.appmovil.ui.theme.ChocolateMedium
                    )
                ) {
                    Text(
                        text = "Iniciar Sesión",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

