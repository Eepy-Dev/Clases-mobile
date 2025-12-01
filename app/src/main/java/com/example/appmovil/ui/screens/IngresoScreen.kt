package com.example.appmovil.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.appmovil.ui.viewmodel.ProductViewModel
import com.example.appmovil.ui.components.ChocoButton

@Composable
fun IngresoScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProductViewModel
) {
    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var imagenUrl by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn() + slideInVertically()
        ) {
            Column {
                Text(
                    text = "Ingreso de Productos",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = precio,
                    onValueChange = { precio = it },
                    label = { Text("Precio") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Stock") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = imagenUrl,
                    onValueChange = { imagenUrl = it },
                    label = { Text("URL Imagen (Opcional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Camera Logic
                val context = androidx.compose.ui.platform.LocalContext.current
                var tempPhotoUri by remember { mutableStateOf<android.net.Uri?>(null) }
                
                val cameraLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
                    contract = androidx.activity.result.contract.ActivityResultContracts.TakePicture()
                ) { success ->
                    if (success && tempPhotoUri != null) {
                        imagenUrl = tempPhotoUri.toString()
                    }
                }

                ChocoButton(
                    text = "Tomar Foto",
                    onClick = {
                        val photoFile = java.io.File(
                            context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES),
                            "product_${System.currentTimeMillis()}.jpg"
                        )
                        val uri = androidx.core.content.FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            photoFile
                        )
                        tempPhotoUri = uri
                        cameraLauncher.launch(uri)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (uiState.isLoading) {
                    CircularProgressIndicator()
                }

                if (uiState.error != null) {
                    Text(text = uiState.error!!, color = MaterialTheme.colorScheme.error)
                }

                ChocoButton(
                    text = "Guardar Producto",
                    onClick = {
                        val p = precio.toDoubleOrNull() ?: 0.0
                        val s = stock.toIntOrNull() ?: 0
                        if (nombre.isNotEmpty() && p > 0 && s >= 0) {
                            viewModel.addProduct(nombre, p, s, imagenUrl)
                            onNavigateBack()
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                ChocoButton(text = "Volver", onClick = onNavigateBack)
            }
        }
    }
}

