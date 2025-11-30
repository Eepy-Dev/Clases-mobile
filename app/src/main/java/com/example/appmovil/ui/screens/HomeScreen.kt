package com.example.appmovil.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmovil.R
import com.example.appmovil.ui.theme.ChocolateMedium
import com.example.appmovil.ui.theme.Cream
import com.example.appmovil.ui.theme.ChocolateDark

@Composable
fun HomeScreen(
    onProductosClick: () -> Unit,
    onConsultaClick: () -> Unit,
    onIngresoClick: () -> Unit,
    onHistorialClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    // Animaciones
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .padding(32.dp)
    ) {
        // Botón de cerrar sesión arriba a la izquierda
        IconButton(
            onClick = onLogoutClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = "Cerrar sesión",
                tint = ChocolateDark,
                modifier = Modifier.size(28.dp)
            )
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo con animacion
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(
                    animationSpec = tween(800, delayMillis = 100)
                ) + scaleIn(
                    initialScale = 0.9f,
                    animationSpec = tween(800, delayMillis = 100)
                ),
                exit = ExitTransition.None
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logos),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .width(254.dp)
                        .height(337.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Botón Productos con animacion
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(600, delayMillis = 400)
                ) + fadeIn(
                    animationSpec = tween(600, delayMillis = 400)
                ),
                exit = ExitTransition.None
            ) {
                Button(
                    onClick = onProductosClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChocolateMedium
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Salida",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Botón Consulta con animacion
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(600, delayMillis = 600)
                ) + fadeIn(
                    animationSpec = tween(600, delayMillis = 600)
                ),
                exit = ExitTransition.None
            ) {
                Button(
                    onClick = onConsultaClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChocolateMedium
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Consulta",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Botón Ingreso con animacion
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(600, delayMillis = 800)
                ) + fadeIn(
                    animationSpec = tween(600, delayMillis = 800)
                ),
                exit = ExitTransition.None
            ) {
                Button(
                    onClick = onIngresoClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChocolateMedium
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Ingreso",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Botón Historial con animacion
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(600, delayMillis = 1000)
                ) + fadeIn(
                    animationSpec = tween(600, delayMillis = 1000)
                ),
                exit = ExitTransition.None
            ) {
                Button(
                    onClick = onHistorialClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChocolateMedium
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Historial",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

