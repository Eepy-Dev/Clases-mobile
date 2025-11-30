package com.example.appmovil.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    onEliminarPorId: (String) -> Unit,
    productoViewModel: ProductoViewModel
) {
    var terminoBusqueda by remember { mutableStateOf("") }
    var productosFiltrados by remember { mutableStateOf<List<Producto>>(emptyList()) }
    var allProductos by remember { mutableStateOf<List<Producto>>(emptyList()) }
    var mensaje by remember { mutableStateOf("") }
    var mostrarResultadosBusqueda by remember { mutableStateOf(false) }
    var idParaEliminar by remember { mutableStateOf("") }
    
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
        productoViewModel.productosFiltrados.observeForever(productosObserver)
        productoViewModel.allProductos.observeForever(allProductosObserver)
        productoViewModel.mensaje.observeForever(mensajeObserver)
        onDispose {
            productoViewModel.productosFiltrados.removeObserver(productosObserver)
            productoViewModel.allProductos.removeObserver(allProductosObserver)
            productoViewModel.mensaje.removeObserver(mensajeObserver)
        }
    }
    
    LaunchedEffect(mensaje) {
        if (mensaje.isNotEmpty()) {
            onMensaje(mensaje)
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
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = terminoBusqueda,
                        onValueChange = { terminoBusqueda = it },
                        label = { Text("Buscar productos") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
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
            
            // Sección eliminar por ID
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(600, delayMillis = 400)
                ) + fadeIn(
                    animationSpec = tween(600, delayMillis = 400)
                ),
                exit = ExitTransition.None
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Eliminar por ID:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChocolateDark
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = idParaEliminar,
                                onValueChange = { idParaEliminar = it },
                                label = { Text("ID del producto") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ChocolateMedium,
                                    unfocusedBorderColor = ChocolateMedium,
                                    focusedLabelColor = ChocolateDark,
                                    unfocusedLabelColor = ChocolateDark,
                                    focusedTextColor = ChocolateDark,
                                    unfocusedTextColor = ChocolateDark
                                )
                            )
                            Button(
                                onClick = {
                                    if (idParaEliminar.trim().isNotEmpty()) {
                                        onEliminarPorId(idParaEliminar.trim())
                                        idParaEliminar = ""
                                    } else {
                                        onMensaje("Ingresa un ID válido")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Red
                                )
                            ) {
                                Text(
                                    text = "Eliminar",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
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
}
