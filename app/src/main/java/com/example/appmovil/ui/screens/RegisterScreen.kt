package com.example.appmovil.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.appmovil.R
import com.example.appmovil.ui.components.ChocoButton
import com.example.appmovil.ui.viewmodel.LoginViewModel

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: LoginViewModel
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    var isVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        isVisible = true
        viewModel.resetState()
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onRegisterSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn() + slideInVertically()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo Choco App",
                    modifier = Modifier.size(150.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Registro",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Usuario") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { 
                        email = it 
                        emailError = if (isValidEmail(it)) null else "Email inválido (use @gmail, @duocuc.cl, @pasteleria.cl)"
                    },
                    label = { Text("Email") },
                    isError = emailError != null,
                    supportingText = { if (emailError != null) Text(emailError!!) },
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

                if (uiState.error != null) {
                    Text(text = uiState.error!!, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                ChocoButton(
                    text = "Registrarse",
                    onClick = {
                        if (username.isNotEmpty() && password.isNotEmpty() && isValidEmail(email)) {
                            viewModel.register(username, password, email)
                        } else if (!isValidEmail(email)) {
                            emailError = "Email inválido"
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                TextButton(onClick = onNavigateBack) {
                    Text("Volver al Login")
                }
            }
        }
    }
}

fun isValidEmail(email: String): Boolean {
    val allowedDomains = listOf("gmail.com", "duocuc.cl", "pasteleria.cl")
    return allowedDomains.any { email.endsWith("@$it") }
}
