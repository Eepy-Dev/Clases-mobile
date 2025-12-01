package com.example.appmovil.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appmovil.ui.viewmodel.ProductViewModel
import com.example.appmovil.ui.components.ChocoButton

@Composable
fun MenuScreen(
    onNavigateToIngreso: () -> Unit,
    onNavigateToConsulta: () -> Unit,
    onNavigateToSalida: () -> Unit,
    onNavigateToCatalog: () -> Unit,
    onNavigateToHistory: () -> Unit,
    viewModel: ProductViewModel
) {
    val userRole by viewModel.userRole.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo (using the same resource as Login for now, assuming it's the ChocoApp logo)
        val infiniteTransition = rememberInfiniteTransition(label = "logoInfinite")
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000),
                repeatMode = RepeatMode.Reverse
            ),
            label = "logoScale"
        )

        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = com.example.appmovil.R.drawable.logo),
            contentDescription = "Logo Choco App",
            modifier = Modifier
                .size(200.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        var buttonsVisible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            buttonsVisible = true
        }

        // Only Admin can delete/adjust stock (Salida)
        if (userRole == "ADMIN") {
            androidx.compose.animation.AnimatedVisibility(
                visible = buttonsVisible,
                enter = androidx.compose.animation.slideInVertically(initialOffsetY = { 50 }) + androidx.compose.animation.fadeIn(),
                modifier = Modifier.fillMaxWidth()
            ) {
                ChocoButton(text = "Salida", onClick = onNavigateToSalida)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        androidx.compose.animation.AnimatedVisibility(
            visible = buttonsVisible,
            enter = androidx.compose.animation.slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(durationMillis = 300, delayMillis = 100)) + androidx.compose.animation.fadeIn(animationSpec = tween(delayMillis = 100)),
            modifier = Modifier.fillMaxWidth()
        ) {
            ChocoButton(text = "Consulta", onClick = onNavigateToConsulta)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Only Admin can create products (Ingreso)
        if (userRole == "ADMIN") {
            androidx.compose.animation.AnimatedVisibility(
                visible = buttonsVisible,
                enter = androidx.compose.animation.slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(durationMillis = 300, delayMillis = 200)) + androidx.compose.animation.fadeIn(animationSpec = tween(delayMillis = 200)),
                modifier = Modifier.fillMaxWidth()
            ) {
                ChocoButton(text = "Ingreso", onClick = onNavigateToIngreso)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        androidx.compose.animation.AnimatedVisibility(
            visible = buttonsVisible,
            enter = androidx.compose.animation.slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(durationMillis = 300, delayMillis = 300)) + androidx.compose.animation.fadeIn(animationSpec = tween(delayMillis = 300)),
            modifier = Modifier.fillMaxWidth()
        ) {
            ChocoButton(text = "Historial", onClick = onNavigateToHistory)
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        androidx.compose.animation.AnimatedVisibility(
            visible = buttonsVisible,
            enter = androidx.compose.animation.slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(durationMillis = 300, delayMillis = 400)) + androidx.compose.animation.fadeIn(animationSpec = tween(delayMillis = 400)),
            modifier = Modifier.fillMaxWidth()
        ) {
            ChocoButton(text = "Catálogo Online", onClick = onNavigateToCatalog)
        }
    }
}
