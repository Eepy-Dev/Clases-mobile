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
import androidx.compose.ui.window.Dialog
import com.example.appmovil.data.Producto
import com.example.appmovil.ui.components.ProductoItem
import com.example.appmovil.ui.theme.ChocolateDark
import com.example.appmovil.ui.theme.ChocolateMedium
import com.example.appmovil.ui.theme.Cream
import com.example.appmovil.ui.viewmodel.ProductoViewModel

@Composable
fun SalidaScreen(
    onVolverClick: () -> Unit,
    onMensaje: (String) -> Unit,
    productoViewModel: ProductoViewModel
) {
    var allProductos by remember { mutableStateOf<List<Producto>>(emptyList()) }
    var mensaje by remember { mutableStateOf("") }
    var terminoBusqueda by remember { mutableStateOf("") }
    var productosFiltrados by remember { mutableStateOf<List<Producto>>(emptyList()) }
    var productoSeleccionado by remember { mutableStateOf<Producto?>(null) }
    var mostrarFormulario by remember { mutableStateOf(false) }
    var tipoSalida by remember { mutableStateOf("Venta") }
    var cantidadSalida by remember { mutableStateOf("") }
    
    DisposableEffect(Unit) {
        val productosObserver = androidx.lifecycle.Observer<List<Producto>> { productos ->
            allProductos = productos
            if (terminoBusqueda.isEmpty()) {
                productosFiltrados = productos
            }
        }
        val mensajeObserver = androidx.lifecycle.Observer<String> { msg ->
            mensaje = msg
        }
        productoViewModel.allProductos.observeForever(productosObserver)
        productoViewModel.mensaje.observeForever(mensajeObserver)
        onDispose {
            productoViewModel.allProductos.removeObserver(productosObserver)
            productoViewModel.mensaje.removeObserver(mensajeObserver)
        }
    }
    
    LaunchedEffect(mensaje) {
        if (mensaje.isNotEmpty()) {
            onMensaje(mensaje)
            productoViewModel.limpiarMensaje()
        }
    }
    
    // Filtrar productos cuando cambia el término de búsqueda
    LaunchedEffect(terminoBusqueda, allProductos) {
        if (terminoBusqueda.isEmpty()) {
            productosFiltrados = allProductos
        } else {
            productosFiltrados = allProductos.filter { producto ->
                producto.nombre.contains(terminoBusqueda, ignoreCase = true) ||
                producto.id.contains(terminoBusqueda, ignoreCase = true)
            }
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
                    text = "Registrar Salida",
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
            
            // Campo de búsqueda
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(600, delayMillis = 200)
                ) + fadeIn(
                    animationSpec = tween(600, delayMillis = 200)
                ),
                exit = ExitTransition.None
            ) {
                OutlinedTextField(
                    value = terminoBusqueda,
                    onValueChange = { terminoBusqueda = it },
                    label = { Text("Buscar producto (ID o Nombre)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChocolateMedium,
                        unfocusedBorderColor = ChocolateMedium,
                        focusedLabelColor = ChocolateDark,
                        unfocusedLabelColor = ChocolateDark,
                        focusedTextColor = ChocolateDark,
                        unfocusedTextColor = ChocolateDark
                    )
                )
            }
            
            // Lista de productos filtrados
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(
                    animationSpec = tween(600, delayMillis = 400)
                ),
                exit = ExitTransition.None
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = productosFiltrados,
                        key = { producto -> producto.id }
                    ) { producto ->
                        ProductoItem(
                            producto = producto,
                            onClick = { 
                                productoSeleccionado = producto
                                mostrarFormulario = true
                            }
                        )
                    }
                }
            }
        }
        
        // Botón Volver fijo en la parte inferior
        Button(
            onClick = onVolverClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter),
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
        
        // Formulario de salida (Dialog)
        if (mostrarFormulario && productoSeleccionado != null) {
            DialogFormularioSalida(
                producto = productoSeleccionado!!,
                tipoSalida = tipoSalida,
                cantidadSalida = cantidadSalida,
                onTipoSalidaChange = { tipoSalida = it },
                onCantidadChange = { cantidadSalida = it },
                onRegistrar = {
                    val cantidad = cantidadSalida.toIntOrNull()
                    if (cantidad == null || cantidad <= 0) {
                        onMensaje("La cantidad debe ser un número válido mayor a 0")
                        return@DialogFormularioSalida
                    }
                    productoViewModel.registrarSalida(productoSeleccionado!!.id, cantidad)
                    mostrarFormulario = false
                    productoSeleccionado = null
                    cantidadSalida = ""
                },
                onCancelar = {
                    mostrarFormulario = false
                    productoSeleccionado = null
                    cantidadSalida = ""
                }
            )
        }
    }
}

@Composable
fun DialogFormularioSalida(
    producto: Producto,
    tipoSalida: String,
    cantidadSalida: String,
    onTipoSalidaChange: (String) -> Unit,
    onCantidadChange: (String) -> Unit,
    onRegistrar: () -> Unit,
    onCancelar: () -> Unit
) {
    Dialog(onDismissRequest = onCancelar) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Registrar Salida",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChocolateDark
                )
                
                Divider(color = ChocolateMedium)
                
                // Información del producto
                Text(
                    text = "Producto: ${producto.nombre}",
                    fontSize = 16.sp,
                    color = ChocolateDark,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "ID: ${producto.id}",
                    fontSize = 14.sp,
                    color = ChocolateMedium
                )
                Text(
                    text = "Stock actual: ${producto.cantidad}",
                    fontSize = 14.sp,
                    color = ChocolateMedium
                )
                
                Divider(color = ChocolateMedium)
                
                // Tipo de salida
                Text(
                    text = "Tipo de salida:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = ChocolateDark
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    RadioButton(
                        selected = tipoSalida == "Venta",
                        onClick = { onTipoSalidaChange("Venta") }
                    )
                    Text(
                        text = "Venta",
                        modifier = Modifier.clickable { onTipoSalidaChange("Venta") },
                        color = ChocolateDark
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    RadioButton(
                        selected = tipoSalida == "Salida común",
                        onClick = { onTipoSalidaChange("Salida común") }
                    )
                    Text(
                        text = "Salida común",
                        modifier = Modifier.clickable { onTipoSalidaChange("Salida común") },
                        color = ChocolateDark
                    )
                }
                
                // Cantidad
                OutlinedTextField(
                    value = cantidadSalida,
                    onValueChange = onCantidadChange,
                    label = { Text("Cantidad a reducir") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChocolateMedium,
                        unfocusedBorderColor = ChocolateMedium,
                        focusedLabelColor = ChocolateDark,
                        unfocusedLabelColor = ChocolateDark
                    )
                )
                
                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onCancelar,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray
                        )
                    ) {
                        Text("Cancelar", color = Color.White)
                    }
                    
                    Button(
                        onClick = onRegistrar,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ChocolateMedium
                        )
                    ) {
                        Text("Registrar", color = Color.White)
                    }
                }
            }
        }
    }
}

