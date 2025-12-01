package com.example.appmovil.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.appmovil.ui.components.ChocoButton
import com.example.appmovil.ui.viewmodel.LoginViewModel
import com.example.appmovil.R

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
        viewModel.resetState()
    }

    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            snackbarHostState.showSnackbar("Inicio de sesión exitoso")
            onLoginSuccess()
        }
    }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            snackbarHostState.showSnackbar(uiState.error!!)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + slideInVertically()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val infiniteTransition = rememberInfiniteTransition(label = "logoInfinite")
                    val scale by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "logoScale"
                    )
                    
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo Choco App",
                        modifier = Modifier
                            .size(200.dp)
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Usuario") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    if (uiState.isLoading) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    ChocoButton(
                        text = "Ingresar",
                        onClick = {
                            if (username.isNotEmpty() && password.isNotEmpty()) {
                                viewModel.login(username, password)
                            }
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Registration disabled for strict access
                    // TextButton(onClick = onNavigateToRegister) {
                    //     Text("¿No tienes cuenta? Regístrate")
                    // }
                    
                    TextButton(onClick = { /* TODO: Implement Forgot Password */ }) {
                        Text("Olvidé mi contraseña")
                    }
                }
            }
        }
    }
}


