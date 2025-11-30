package com.example.appmovil.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appmovil.domain.model.Product
import com.example.appmovil.ui.viewmodel.ProductViewModel
import com.example.appmovil.ui.components.ChocoButton

@Composable
fun ConsultaScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProductViewModel
) {
    var searchQuery by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
        viewModel.loadProducts()
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
                    text = "Consulta de Productos",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Buscar por nombre") },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ChocoButton(
                        text = "Buscar",
                        onClick = { viewModel.searchProducts(searchQuery) },
                        modifier = Modifier.width(100.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                LazyColumn {
                    items(uiState.products) { product ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically()
                        ) {
                            ProductItem(
                                product = product,
                                onDelete = { viewModel.deleteProduct(product.id!!) }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                ChocoButton(text = "Volver", onClick = onNavigateBack)
            }
        }
    }
}

@Composable
fun ProductItem(product: Product, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = product.nombre, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Text(text = "ID: ${product.id} | Stock: ${product.stock} | $${product.precio}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

