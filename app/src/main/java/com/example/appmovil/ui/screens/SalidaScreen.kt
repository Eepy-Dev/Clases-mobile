package com.example.appmovil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.appmovil.ui.viewmodel.ProductViewModel

@Composable
fun SalidaScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProductViewModel
) {
    var idProducto by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Salida de Productos", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = idProducto,
            onValueChange = { idProducto = it },
            label = { Text("ID del Producto") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = cantidad,
            onValueChange = { cantidad = it },
            label = { Text("Cantidad a retirar") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator()
        }

        if (uiState.error != null) {
            Text(text = uiState.error!!, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = {
                val id = idProducto.toLongOrNull()
                val cant = cantidad.toIntOrNull()
                if (id != null && cant != null && cant > 0) {
                    viewModel.registerOutput(id, cant)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar Salida")
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onNavigateBack, modifier = Modifier.fillMaxWidth()) {
            Text("Volver")
        }
    }
}
