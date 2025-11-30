package com.example.appmovil.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.appmovil.ui.viewmodel.ProductViewModel
import com.example.appmovil.ui.components.ChocoButton

@Composable
fun MenuScreen(
    onNavigateToIngreso: () -> Unit,
    onNavigateToConsulta: () -> Unit,
    onNavigateToSalida: () -> Unit,
    viewModel: ProductViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
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
                Text(
                    text = "Menú Principal",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(32.dp))

                ChocoButton(text = "Ingreso de Productos", onClick = onNavigateToIngreso)
                Spacer(modifier = Modifier.height(16.dp))

                ChocoButton(text = "Consulta de Productos", onClick = onNavigateToConsulta)
                Spacer(modifier = Modifier.height(16.dp))

                ChocoButton(text = "Salida de Productos", onClick = onNavigateToSalida)

                Spacer(modifier = Modifier.height(32.dp))

                if (uiState.externalImageUrl != null) {
                    Text("Imagen Aleatoria (API Externa):", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    AsyncImage(
                        model = uiState.externalImageUrl,
                        contentDescription = "Random Dog",
                        modifier = Modifier.size(200.dp)
                    )
                }
            }
        }
    }
}

