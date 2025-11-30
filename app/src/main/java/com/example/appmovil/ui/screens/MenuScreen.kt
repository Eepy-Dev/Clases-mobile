package com.example.appmovil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.appmovil.ui.viewmodel.ProductViewModel

@Composable
fun MenuScreen(
    onNavigateToIngreso: () -> Unit,
    onNavigateToConsulta: () -> Unit,
    onNavigateToSalida: () -> Unit,
    viewModel: ProductViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Menú Principal", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onNavigateToIngreso, modifier = Modifier.fillMaxWidth()) {
            Text("Ingreso de Productos")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onNavigateToConsulta, modifier = Modifier.fillMaxWidth()) {
            Text("Consulta de Productos")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onNavigateToSalida, modifier = Modifier.fillMaxWidth()) {
            Text("Salida de Productos")
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (uiState.externalImageUrl != null) {
            Text("Imagen Aleatoria (API Externa):")
            AsyncImage(
                model = uiState.externalImageUrl,
                contentDescription = "Random Dog",
                modifier = Modifier.size(200.dp)
            )
        }
    }
}
