package com.example.appmovil.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.appmovil.data.ProductoExterno
import com.example.appmovil.ui.theme.ChocolateDark
import com.example.appmovil.ui.theme.ChocolateMedium
import com.example.appmovil.ui.theme.Cream
import com.example.appmovil.ui.viewmodel.ProductoViewModel

@Composable
fun ProductosExternosScreen(
    productoViewModel: ProductoViewModel,
    onBackClick: () -> Unit,
    onAgregarAlInventario: (ProductoExterno) -> Unit
) {
    var productosExternos by remember { mutableStateOf<List<ProductoExterno>>(emptyList()) }
    var cargando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    
    DisposableEffect(Unit) {
        val productosObserver = androidx.lifecycle.Observer<List<ProductoExterno>> { productos ->
            productosExternos = productos
        }
        val cargandoObserver = androidx.lifecycle.Observer<Boolean> { isLoading ->
            cargando = isLoading
        }
        val errorObserver = androidx.lifecycle.Observer<String?> { errorMsg ->
            error = errorMsg
        }
        
        productoViewModel.productosExternos.observeForever(productosObserver)
        productoViewModel.cargandoProductosExternos.observeForever(cargandoObserver)
        productoViewModel.errorProductosExternos.observeForever(errorObserver)
        
        // Cargar productos al iniciar
        productoViewModel.obtenerProductosExternos()
        
        onDispose {
            productoViewModel.productosExternos.removeObserver(productosObserver)
            productoViewModel.cargandoProductosExternos.removeObserver(cargandoObserver)
            productoViewModel.errorProductosExternos.removeObserver(errorObserver)
        }
    }
    
    // Animaciones
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Barra superior con título y botones
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = ChocolateMedium,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                    
                    Text(
                        text = "Catálogo Online",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.weight(1f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    
                    IconButton(
                        onClick = { productoViewModel.obtenerProductosExternos() },
                        enabled = !cargando
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Actualizar",
                            tint = Color.White
                        )
                    }
                }
            }
            
            // Contenido
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
                    initialOffsetY = { 30 },
                    animationSpec = tween(600)
                ),
                exit = ExitTransition.None
            ) {
                when {
                    cargando -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                CircularProgressIndicator(
                                    color = ChocolateMedium,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    text = "Cargando productos...",
                                    fontSize = 16.sp,
                                    color = ChocolateDark
                                )
                            }
                        }
                    }
                    
                    error != null -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "Error al cargar productos",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Red
                                )
                                Text(
                                    text = error ?: "Error desconocido",
                                    fontSize = 14.sp,
                                    color = ChocolateDark
                                )
                                Button(
                                    onClick = { productoViewModel.obtenerProductosExternos() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ChocolateMedium
                                    )
                                ) {
                                    Text("Reintentar", color = Color.White)
                                }
                            }
                        }
                    }
                    
                    productosExternos.isEmpty() -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay productos disponibles",
                                fontSize = 16.sp,
                                color = ChocolateDark
                            )
                        }
                    }
                    
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(productosExternos) { producto ->
                                ProductoExternoItem(
                                    producto = producto,
                                    onAgregarClick = { onAgregarAlInventario(producto) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductoExternoItem(
    producto: ProductoExterno,
    onAgregarClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Imagen del producto
            Image(
                painter = rememberAsyncImagePainter(producto.imagenUrl),
                contentDescription = producto.title,
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        Color.LightGray.copy(alpha = 0.3f),
                        RoundedCornerShape(8.dp)
                    ),
                contentScale = ContentScale.Crop
            )
            
            // Información del producto
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = producto.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChocolateDark,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = "$${String.format("%.2f", producto.price)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChocolateMedium
                )
                
                producto.rating?.let { rating ->
                    Text(
                        text = "⭐ ${rating.rate} (${rating.count} reseñas)",
                        fontSize = 12.sp,
                        color = ChocolateDark
                    )
                }
            }
            
            // Botón para agregar al inventario
            Button(
                onClick = onAgregarClick,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChocolateMedium
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Agregar",
                    fontSize = 12.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

