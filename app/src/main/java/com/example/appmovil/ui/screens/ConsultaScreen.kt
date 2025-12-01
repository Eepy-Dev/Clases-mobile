package com.example.appmovil.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.appmovil.domain.model.Product
import com.example.appmovil.ui.viewmodel.ProductViewModel
import com.example.appmovil.ui.components.ChocoButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultaScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    viewModel: ProductViewModel
) {
    var searchQuery by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadProducts()
    }

    // Filter products for suggestions
    val suggestions = remember(searchQuery, uiState.products) {
        if (searchQuery.isBlank()) emptyList()
        else uiState.products.filter { it.nombre.contains(searchQuery, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Consulta de Productos",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar with Predictions
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onSearch = {
                active = false
                viewModel.searchProducts(searchQuery)
            },
            active = active,
            onActiveChange = { active = it },
            placeholder = { Text("Buscar chocolate, galleta...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (active) {
                    Icon(
                        modifier = Modifier.clickable {
                            if (searchQuery.isNotEmpty()) {
                                searchQuery = ""
                            } else {
                                active = false
                            }
                        },
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyColumn {
                items(suggestions) { product ->
                    ListItem(
                        headlineContent = { Text(product.nombre) },
                        leadingContent = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier.clickable {
                            searchQuery = product.nombre
                            active = false
                            viewModel.searchProducts(product.nombre)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.products, key = { it.id ?: 0 }) { product ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically()
                ) {
                    ProductItem(
                        product = product,
                        onClick = { onNavigateToDetail(product.id!!) },
                        onDelete = {
                            viewModel.deleteProduct(product.id!!)
                            Toast.makeText(context, "Producto eliminado", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        ChocoButton(text = "Volver", onClick = onNavigateBack)
    }
}

@Composable
fun ProductItem(product: Product, onClick: () -> Unit, onDelete: () -> Unit) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail
            AsyncImage(
                model = product.imagenUrl,
                contentDescription = product.nombre,
                modifier = Modifier
                    .size(64.dp)
                    .padding(4.dp),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = product.nombre, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Text(text = "ID: ${product.id} | Stock: ${product.stock}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "$${product.precio}", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            }
            
            Row {
                IconButton(onClick = {
                    val message = "¡Mira este producto en ChocoApp!\n\n*${product.nombre}*\nPrecio: $${product.precio}\nStock: ${product.stock}\n\n${product.imagenUrl ?: ""}"
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, message)
                        setPackage("com.whatsapp")
                    }
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "WhatsApp no instalado, abriendo selector...", Toast.LENGTH_SHORT).show()
                        val shareIntent = Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, message)
                        }, "Compartir vía")
                        context.startActivity(shareIntent)
                    }
                }) {
                    Icon(Icons.Default.Share, contentDescription = "Compartir", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
