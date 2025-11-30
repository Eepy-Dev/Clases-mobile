package com.example.appmovil.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import com.example.appmovil.util.ProductoValidator

@Composable
fun IngresoScreen(
    onVolverClick: () -> Unit,
    onMensaje: (String) -> Unit,
    onTomarFoto: () -> Unit,
    productoViewModel: ProductoViewModel,
    esModoEdicion: Boolean,
    productoEditando: Producto?
) {
    var id by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var rutaFoto by remember { mutableStateOf<String?>(null) }
    var imagenBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var productoSeleccionado by remember { mutableStateOf<Producto?>(null) }
    var mensaje by remember { mutableStateOf("") }
    var imagenCapturada by remember { mutableStateOf<Bitmap?>(null) }
    var rutaImagenCapturada by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    
    DisposableEffect(Unit) {
        val productoObserver = androidx.lifecycle.Observer<Producto?> { producto ->
            productoSeleccionado = producto
        }
        val mensajeObserver = androidx.lifecycle.Observer<String> { msg ->
            mensaje = msg
        }
        val imagenObserver = androidx.lifecycle.Observer<Pair<Bitmap?, String?>> { (bitmap, ruta) ->
            imagenCapturada = bitmap
            rutaImagenCapturada = ruta
            if (bitmap != null) {
                imagenBitmap = bitmap
            }
            if (ruta != null) {
                rutaFoto = ruta
            }
        }
        productoViewModel.productoSeleccionado.observeForever(productoObserver)
        productoViewModel.mensaje.observeForever(mensajeObserver)
        productoViewModel.imagenCapturada.observeForever(imagenObserver)
        onDispose {
            productoViewModel.productoSeleccionado.removeObserver(productoObserver)
            productoViewModel.mensaje.removeObserver(mensajeObserver)
            productoViewModel.imagenCapturada.removeObserver(imagenObserver)
        }
    }
    
    // Cargar datos del producto si está en modo edición
    LaunchedEffect(productoSeleccionado) {
        productoSeleccionado?.let { producto ->
            id = producto.id
            nombre = producto.nombre
            descripcion = producto.descripcion
            precio = producto.precio.toString()
            cantidad = producto.cantidad.toString()
            rutaFoto = producto.foto
            if (producto.foto != null) {
                val bitmap = ImageUtils.cargarImagen(producto.foto)
                if (bitmap != null) {
                    imagenBitmap = bitmap
                }
            }
        }
    }
    
    LaunchedEffect(mensaje) {
        if (mensaje.isNotEmpty()) {
            onMensaje(mensaje)
            productoViewModel.limpiarMensaje()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .verticalScroll(rememberScrollState())
    ) {
        // Título
        Text(
            text = "Ingreso de Productos",
            fontSize = 24.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(ChocolateMedium)
                .padding(24.dp)
        )
        
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Campo ID
            OutlinedTextField(
                value = id,
                onValueChange = { id = it },
                label = { Text("ID del Producto") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
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
            
            // Campo Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del Producto") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
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
            
            // Campo Descripción
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(vertical = 4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ChocolateMedium,
                    unfocusedBorderColor = ChocolateMedium,
                    focusedLabelColor = ChocolateDark,
                    unfocusedLabelColor = ChocolateDark,
                    focusedTextColor = ChocolateDark,
                    unfocusedTextColor = ChocolateDark
                ),
                shape = RoundedCornerShape(8.dp)
            )
            
            // Campo Precio
            OutlinedTextField(
                value = precio,
                onValueChange = { precio = it },
                label = { Text("Precio") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
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
            
            // Campo Cantidad
            OutlinedTextField(
                value = cantidad,
                onValueChange = { cantidad = it },
                label = { Text("Cantidad") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
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
            
            // Imagen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(CreamDark)
            ) {
                if (imagenBitmap != null) {
                    Image(
                        bitmap = imagenBitmap!!.asImageBitmap(),
                        contentDescription = "Foto del producto",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = android.R.drawable.ic_menu_camera),
                        contentDescription = "Tomar foto",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                }
            }
            
            // Botón Tomar Foto
            Button(
                onClick = onTomarFoto,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChocolateMedium
                )
            ) {
                Text(
                    text = "Tomar Foto",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Botón Guardar
            Button(
                onClick = {
                    scope.launch {
                        val exito = ProductoValidator.guardarProducto(
                            id, nombre, descripcion, precio, cantidad, rutaFoto,
                            esModoEdicion, productoViewModel, onMensaje
                        )
                        if (exito) {
                            // Limpiar campos después de guardar exitosamente
                            id = ""
                            nombre = ""
                            descripcion = ""
                            precio = ""
                            cantidad = ""
                            rutaFoto = null
                            imagenBitmap = null
                            imagenCapturada = null
                            rutaImagenCapturada = null
                            // El mensaje de éxito ya se muestra desde el ViewModel
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChocolateMedium
                )
            ) {
                Text(
                    text = "Guardar Producto",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Botón Volver
            Button(
                onClick = onVolverClick,
                modifier = Modifier.fillMaxWidth(),
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

