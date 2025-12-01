package com.example.appmovil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appmovil.data.local.entity.DeletedProductEntity
import com.example.appmovil.ui.viewmodel.ProductViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    viewModel: ProductViewModel,
    onNavigateBack: () -> Unit
) {
    var deletedProducts by remember { mutableStateOf<List<DeletedProductEntity>>(emptyList()) }

    LaunchedEffect(Unit) {
        viewModel.loadDeletedProducts { products ->
            deletedProducts = products
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Historial de Eliminados",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(deletedProducts) { product ->
                HistoryItem(product)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigateBack) {
            Text("Volver")
        }
    }
}

@Composable
fun HistoryItem(product: DeletedProductEntity) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(text = product.nombre, style = MaterialTheme.typography.titleMedium)
            Text(text = "Precio: $${product.precio.toInt()}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Eliminado el: ${dateFormat.format(Date(product.deletedAt))}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
