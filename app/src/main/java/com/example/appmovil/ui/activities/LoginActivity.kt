package com.example.appmovil.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.lifecycle.ViewModelProvider
import com.example.appmovil.R
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import com.example.appmovil.ui.viewmodel.LoginViewModel
import com.example.appmovil.ui.theme.AppMovilTheme
import com.example.appmovil.ui.theme.ChocolateDark
import com.example.appmovil.ui.theme.ChocolateLight
import com.example.appmovil.ui.theme.Cream

class LoginActivity : ComponentActivity() {
    
    private lateinit var loginViewModel: LoginViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setupViewModel()
        
        setContent {
            AppMovilTheme {
                LoginScreen(
                    onLoginSuccess = {
                        val intent = Intent(this, com.example.appmovil.ui.activities.HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    },
                    loginViewModel = loginViewModel
                )
            }
        }
    }
    
    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(this, AndroidViewModelFactory.getInstance(application))[LoginViewModel::class.java]
    }
}

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    loginViewModel: LoginViewModel
) {
    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var loginResult by remember { mutableStateOf(false) }
    var errorUsuario by remember { mutableStateOf("") }
    var errorContrasena by remember { mutableStateOf("") }
    
    DisposableEffect(Unit) {
        val loginObserver = androidx.lifecycle.Observer<Boolean> { result ->
            loginResult = result
        }
        val errorUsuarioObserver = androidx.lifecycle.Observer<String> { message ->
            errorUsuario = message
        }
        val errorContrasenaObserver = androidx.lifecycle.Observer<String> { message ->
            errorContrasena = message
        }
        loginViewModel.loginResult.observeForever(loginObserver)
        loginViewModel.errorUsuario.observeForever(errorUsuarioObserver)
        loginViewModel.errorContrasena.observeForever(errorContrasenaObserver)
        onDispose {
            loginViewModel.loginResult.removeObserver(loginObserver)
            loginViewModel.errorUsuario.removeObserver(errorUsuarioObserver)
            loginViewModel.errorContrasena.removeObserver(errorContrasenaObserver)
        }
    }
    
    LaunchedEffect(loginResult) {
        if (loginResult) {
            onLoginSuccess()
        }
    }
    
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .padding(32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logos),
                contentDescription = "Logo",
                modifier = Modifier
                    .width(254.dp)
                    .height(217.dp)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Título de bienvenida
            Text(
                text = "Bienvenidos!",
                color = ChocolateDark,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(11.dp)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Campo de usuario
            OutlinedTextField(
                value = usuario,
                onValueChange = { 
                    usuario = it
                    errorUsuario = ""
                },
                label = { Text("Usuario") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = errorUsuario.isNotEmpty(),
                supportingText = if (errorUsuario.isNotEmpty()) {
                    { Text(errorUsuario, color = Color.Red, fontSize = 12.sp) }
                } else null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (errorUsuario.isEmpty()) ChocolateLight else Color.Red,
                    unfocusedBorderColor = if (errorUsuario.isEmpty()) ChocolateLight else Color.Red,
                    focusedLabelColor = ChocolateDark,
                    unfocusedLabelColor = ChocolateDark,
                    focusedTextColor = ChocolateDark,
                    unfocusedTextColor = ChocolateDark
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Campo de contraseña
            OutlinedTextField(
                value = contrasena,
                onValueChange = { 
                    contrasena = it
                    errorContrasena = ""
                },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = errorContrasena.isNotEmpty(),
                supportingText = if (errorContrasena.isNotEmpty()) {
                    { Text(errorContrasena, color = Color.Red, fontSize = 12.sp) }
                } else null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (errorContrasena.isEmpty()) ChocolateLight else Color.Red,
                    unfocusedBorderColor = if (errorContrasena.isEmpty()) ChocolateLight else Color.Red,
                    focusedLabelColor = ChocolateDark,
                    unfocusedLabelColor = ChocolateDark,
                    focusedTextColor = ChocolateDark,
                    unfocusedTextColor = ChocolateDark
                )
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Botón de login
            Button(
                onClick = {
                    loginViewModel.validarLogin(usuario.trim(), contrasena.trim())
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
