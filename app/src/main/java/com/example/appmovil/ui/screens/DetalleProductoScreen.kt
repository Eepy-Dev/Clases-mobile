package com.example.appmovil.ui.screens

import android.graphics.Bitmap
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmovil.data.Producto
import com.example.appmovil.ui.theme.ChocolateDark
import com.example.appmovil.ui.theme.ChocolateMedium
import com.example.appmovil.ui.theme.Cream
import com.example.appmovil.ui.theme.CreamDark
import com.example.appmovil.ui.viewmodel.ProductoViewModel
import com.example.appmovil.util.ImageUtils

@Composable
fun DetalleProductoScreen(
    producto: Producto,
    onVolverClick: () -> Unit,
    onCompartirClick: (Producto) -> Unit,
    onEditarClick: (Producto) -> Unit,
    onEliminarClick: (Producto) -> Unit,
    productoViewModel: ProductoViewModel
) {
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }
    
    DisposableEffect(Unit) {
        val mensajeObserver = androidx.lifecycle.Observer<String> { msg ->
            mensaje = msg
        }
        productoViewModel.mensaje.observeForever(mensajeObserver)
        onDispose {
            productoViewModel.mensaje.removeObserver(mensajeObserver)
        }
    }
    var imagenBitmap by remember { mutableStateOf<Bitmap?>(null) }
    
    // Animaciones
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    // Cargar imagen si existe
    LaunchedEffect(producto.foto) {
        if (producto.foto != null) {
            val bitmap = ImageUtils.cargarImagen(producto.foto)
            if (bitmap != null) {
                imagenBitmap = bitmap
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .verticalScroll(rememberScrollState())
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
                text = "Detalles del Producto",
                fontSize = 24.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ChocolateMedium)
                    .padding(24.dp)
            )
        }
        
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Imagen del producto con animacion
            AnimatedVisibility(
                visible = isVisible && imagenBitmap != null,
                enter = scaleIn(
                    initialScale = 0.8f,
                    animationSpec = tween(600, delayMillis = 300)
                ) + fadeIn(
                    animationSpec = tween(600, delayMillis = 300)
                ),
                exit = ExitTransition.None
            ) {
                if (imagenBitmap != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Image(
                        bitmap = imagenBitmap!!.asImageBitmap(),
                        contentDescription = "Foto del producto",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            }
            
            AnimatedVisibility(
                visible = isVisible && imagenBitmap == null,
                enter = fadeIn(
                    animationSpec = tween(400, delayMillis = 300)
                ),
                exit = ExitTransition.None
            ) {
                if (imagenBitmap == null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = CreamDark)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = android.R.drawable.ic_menu_camera),
                                contentDescription = "Sin imagen",
                                modifier = Modifier.size(64.dp)
                            )
                        }
                    }
                }
            }
            
            // Información del producto con animacion
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { it / 2 },
                    animationSpec = tween(600, delayMillis = 500)
                ) + fadeIn(
                    animationSpec = tween(600, delayMillis = 500)
                ),
                exit = ExitTransition.None
            ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // ID
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "ID:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChocolateDark
                        )
                        Text(
                            text = producto.id,
                            fontSize = 16.sp,
                            color = ChocolateMedium
                        )
                    }
                    
                    Divider(color = ChocolateMedium, thickness = 1.dp)
                    
                    // Nombre
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Nombre:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChocolateDark
                        )
                        Text(
                            text = producto.nombre,
                            fontSize = 16.sp,
                            color = ChocolateMedium
                        )
                    }
                    
                    Divider(color = ChocolateMedium, thickness = 1.dp)
                    
                    // Descripción
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Descripción:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChocolateDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = producto.descripcion,
                            fontSize = 16.sp,
                            color = ChocolateMedium
                        )
                    }
                    
                    Divider(color = ChocolateMedium, thickness = 1.dp)
                    
                    // Precio
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Precio:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChocolateDark
                        )
                        Text(
                            text = "$${producto.precio}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChocolateMedium
                        )
                    }
                    
                    Divider(color = ChocolateMedium, thickness = 1.dp)
                    
                    // Cantidad
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Stock:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChocolateDark
                        )
                        Text(
                            text = "${producto.cantidad} unidades",
                            fontSize = 16.sp,
                            color = ChocolateMedium
                        )
                    }
                }
            }
            }
            
            // Botones con animacion
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(600, delayMillis = 700)
                ) + fadeIn(
                    animationSpec = tween(600, delayMillis = 700)
                ),
                exit = ExitTransition.None
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Fila de botones principales
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Botón Volver
                        Button(
                            onClick = onVolverClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ChocolateMedium
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Volver",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        // Botón Compartir
                        Button(
                            onClick = { onCompartirClick(producto) },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = com.example.appmovil.ui.theme.ChocolateLight
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Compartir",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Botón Editar
                        Button(
                            onClick = { onEditarClick(producto) },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ChocolateMedium
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Editar",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Botón Eliminar
                    Button(
                        onClick = { mostrarDialogoEliminar = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Eliminar Producto",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
    
    // Dialog de confirmación para eliminar
    if (mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            title = {
                Text(
                    text = "Eliminar Producto",
                    fontWeight = FontWeight.Bold,
                    color = ChocolateDark
                )
            },
            text = {
                Text(
                    text = "¿Estás seguro de que quieres eliminar el producto '${producto.nombre}'?\n\nEsta acción no se puede deshacer.",
                    color = ChocolateDark
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoEliminar = false
                        onEliminarClick(producto)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarDialogoEliminar = false }
                ) {
                    Text("Cancelar", color = ChocolateMedium)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

