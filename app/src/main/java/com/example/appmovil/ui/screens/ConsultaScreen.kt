package com.example.appmovil.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmovil.data.Producto
import com.example.appmovil.ui.components.ProductoItem
import com.example.appmovil.ui.theme.ChocolateDark
import com.example.appmovil.ui.theme.ChocolateMedium
import com.example.appmovil.ui.theme.Cream
import com.example.appmovil.ui.viewmodel.ProductoViewModel

@Composable
fun ConsultaScreen(
    onVolverClick: () -> Unit,
    onProductoClick: (Producto) -> Unit,
    onMensaje: (String) -> Unit,
    productoViewModel: ProductoViewModel
) {
    var terminoBusqueda by remember { mutableStateOf("") }
    var productosFiltrados by remember { mutableStateOf<List<Producto>>(emptyList()) }
    var allProductos by remember { mutableStateOf<List<Producto>>(emptyList()) }
    var mensaje by remember { mutableStateOf("") }
    var mostrarResultadosBusqueda by remember { mutableStateOf(false) }
    var mostrarDialogoSincronizar by remember { mutableStateOf(false) }
    var cargandoSincronizacion by remember { mutableStateOf(false) }
    
    DisposableEffect(Unit) {
        val productosObserver = androidx.lifecycle.Observer<List<Producto>> { productos ->
            productosFiltrados = productos
            mostrarResultadosBusqueda = true
        }
        val allProductosObserver = androidx.lifecycle.Observer<List<Producto>> { productos ->
            allProductos = productos
        }
        val mensajeObserver = androidx.lifecycle.Observer<String> { msg ->
            mensaje = msg
        }
        val cargandoObserver = androidx.lifecycle.Observer<Boolean> { isLoading ->
            cargandoSincronizacion = isLoading
        }
        productoViewModel.productosFiltrados.observeForever(productosObserver)
        productoViewModel.allProductos.observeForever(allProductosObserver)
        productoViewModel.mensaje.observeForever(mensajeObserver)
        productoViewModel.cargandoProductosExternos.observeForever(cargandoObserver)
        onDispose {
            productoViewModel.productosFiltrados.removeObserver(productosObserver)
            productoViewModel.allProductos.removeObserver(allProductosObserver)
            productoViewModel.mensaje.removeObserver(mensajeObserver)
            productoViewModel.cargandoProductosExternos.removeObserver(cargandoObserver)
        }
    }
    
    LaunchedEffect(mensaje) {
        if (mensaje.isNotEmpty()) {
            if (mensaje.contains("Productos nuevos agregados", ignoreCase = true)) {
                onMensaje("Productos nuevos agregados al servidor")
            } else {
                onMensaje(mensaje)
            }
            productoViewModel.limpiarMensaje()
        }
    }
    
    // Lista de productos a mostrar (inicial: primeros 10, o resultados de búsqueda)
    val productosAMostrar = if (mostrarResultadosBusqueda) {
        productosFiltrados
    } else {
        allProductos.take(10) // Mostrar solo los primeros 10 productos inicialmente
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
            // Título con animacion
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(600, delayMillis = 100)
                ) + fadeIn(
                    animationSpec = tween(600, delayMillis = 100)
                ),
                exit = ExitTransition.None
            ) {
                Text(
                    text = "Consulta de Productos",
                    fontSize = 24.sp,
                    color = ChocolateDark,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ChocolateMedium)
                        .padding(24.dp)
                )
            }
            
            // Botón Sincronizar con Servidor
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(
                    animationSpec = tween(400, delayMillis = 200)
                ),
                exit = ExitTransition.None
            ) {
                Button(
                    onClick = { mostrarDialogoSincronizar = true },
                    enabled = !cargandoSincronizacion,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChocolateMedium
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (cargandoSincronizacion) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Sincronizar con el Servidor",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            // Campo de búsqueda y botón con animacion
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(600, delayMillis = 300)
                ) + fadeIn(
                    animationSpec = tween(600, delayMillis = 300)
                ),
                exit = ExitTransition.None
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = terminoBusqueda,
                        onValueChange = { terminoBusqueda = it },
                        label = { Text("Buscar productos") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 4.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ChocolateMedium,
                            unfocusedBorderColor = ChocolateMedium,
                            focusedLabelColor = ChocolateDark,
                            unfocusedLabelColor = ChocolateDark,
                            focusedTextColor = ChocolateDark,
                            unfocusedTextColor = ChocolateDark
                        )
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            if (terminoBusqueda.trim().isNotEmpty()) {
                                productoViewModel.buscarProductos(terminoBusqueda.trim())
                                mostrarResultadosBusqueda = true
                            } else {
                                mostrarResultadosBusqueda = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ChocolateMedium
                        )
                    ) {
                        Text(
                            text = "Buscar",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            // Lista de productos (inicial o resultados de búsqueda)
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(
                    animationSpec = tween(600, delayMillis = 500)
                ),
                exit = ExitTransition.None
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (productosAMostrar.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                )
                            ) {
                                Text(
                                    text = if (mostrarResultadosBusqueda) "No se encontraron productos" else "No hay productos disponibles",
                                    modifier = Modifier.padding(16.dp),
                                    textAlign = TextAlign.Center,
                                    color = ChocolateMedium
                                )
                            }
                        }
                    } else {
                        items(
                            items = productosAMostrar,
                            key = { producto -> producto.id }
                        ) { producto ->
                            ProductoItem(
                                producto = producto,
                                onClick = { onProductoClick(producto) }
                            )
                        }
                    }
                }
            }
            
            // Botón Volver (no fijo en el bottom, más accesible)
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(
                    animationSpec = tween(400, delayMillis = 600)
                ),
                exit = ExitTransition.None
            ) {
                Button(
                    onClick = onVolverClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChocolateMedium
                    )
                ) {
                    Text(
                        text = "Volver",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
    
    // Dialog de confirmación para sincronización masiva
    if (mostrarDialogoSincronizar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoSincronizar = false },
            title = {
                Text(
                    text = "Sincronizar con el Servidor",
                    fontWeight = FontWeight.Bold,
                    color = ChocolateDark
                )
            },
            text = {
                Text(
                    text = "¿Estás seguro de que quieres sincronizar todos los productos locales con el servidor?\n\nSolo se agregarán los productos que no existan en el servidor. Los productos que ya existen serán omitidos.",
                    color = ChocolateDark
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoSincronizar = false
                        productoViewModel.sincronizarTodosLosProductosAlServidor()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChocolateMedium
                    )
                ) {
                    Text("Sincronizar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarDialogoSincronizar = false }
                ) {
                    Text("Cancelar", color = ChocolateMedium)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

