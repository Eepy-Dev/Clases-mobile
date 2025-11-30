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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import coil.compose.rememberAsyncImagePainter
import com.example.appmovil.data.Producto
import com.example.appmovil.ui.theme.ChocolateDark
import com.example.appmovil.ui.theme.ChocolateMedium
import com.example.appmovil.ui.theme.Cream
import com.example.appmovil.ui.theme.CreamDark
import com.example.appmovil.ui.viewmodel.ProductoViewModel
import com.example.appmovil.util.ImageUtils
import com.example.appmovil.util.ProductoValidator
import com.example.appmovil.util.ValidationResult

@Composable
fun IngresoScreen(
    onVolverClick: () -> Unit,
    onMensaje: (String) -> Unit,
    onSeleccionarImagen: () -> Unit,
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
    
    // Errores de validación por campo
    var erroresValidacion by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    
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
    
    // Cargar datos del producto si está en modo edición o viene del catálogo
    LaunchedEffect(productoSeleccionado, productoEditando) {
        val productoACargar = productoSeleccionado ?: productoEditando
        productoACargar?.let { producto ->
            id = producto.id
            nombre = producto.nombre
            descripcion = producto.descripcion
            precio = producto.precio.toString()
            cantidad = producto.cantidad.toString()
            rutaFoto = producto.foto
            if (producto.foto != null) {
                // Si es una URL (del catálogo), cargar con Coil o similar
                if (producto.foto.startsWith("http")) {
                    // Es una URL, se puede cargar después si es necesario
                    rutaFoto = producto.foto
                } else {
                    // Es una ruta local
                    val bitmap = ImageUtils.cargarImagen(producto.foto)
                    if (bitmap != null) {
                        imagenBitmap = bitmap
                    }
                }
            }
        }
    }
    
    // Solo mostrar mensajes de éxito del ViewModel (no errores de validación)
    LaunchedEffect(mensaje) {
        if (mensaje.isNotEmpty() && mensaje.contains("exitosamente", ignoreCase = true)) {
            onMensaje(mensaje)
            productoViewModel.limpiarMensaje()
        } else if (mensaje.isNotEmpty()) {
            // Otros mensajes del ViewModel (errores de base de datos, etc.)
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
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = id,
                    onValueChange = { 
                        if (!esModoEdicion) { // Solo permitir editar si NO está en modo edición
                            id = it
                            // Limpiar error cuando el usuario empieza a escribir
                            erroresValidacion = erroresValidacion.filterKeys { it != "id" }
                        }
                    },
                    label = { Text("ID del Producto") },
                    singleLine = true,
                    enabled = !esModoEdicion, // Deshabilitar en modo edición
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    isError = erroresValidacion.containsKey("id"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (erroresValidacion.containsKey("id")) Color.Red else ChocolateMedium,
                        unfocusedBorderColor = if (erroresValidacion.containsKey("id")) Color.Red else ChocolateMedium,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = ChocolateDark,
                        unfocusedLabelColor = ChocolateDark,
                        errorLabelColor = Color.Red,
                        focusedTextColor = ChocolateDark,
                        unfocusedTextColor = ChocolateDark,
                        disabledTextColor = ChocolateDark.copy(alpha = 0.6f),
                        disabledLabelColor = ChocolateDark.copy(alpha = 0.6f),
                        disabledBorderColor = ChocolateMedium.copy(alpha = 0.5f)
                    )
                )
                // Mensaje de error debajo del campo
                erroresValidacion["id"]?.let { error ->
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }
            
            // Campo Nombre
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { 
                        nombre = it
                        erroresValidacion = erroresValidacion.filterKeys { it != "nombre" }
                    },
                    label = { Text("Nombre del Producto") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    isError = erroresValidacion.containsKey("nombre"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (erroresValidacion.containsKey("nombre")) Color.Red else ChocolateMedium,
                        unfocusedBorderColor = if (erroresValidacion.containsKey("nombre")) Color.Red else ChocolateMedium,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = ChocolateDark,
                        unfocusedLabelColor = ChocolateDark,
                        errorLabelColor = Color.Red,
                        focusedTextColor = ChocolateDark,
                        unfocusedTextColor = ChocolateDark
                    )
                )
                erroresValidacion["nombre"]?.let { error ->
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }
            
            // Campo Descripción
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { 
                        descripcion = it
                        erroresValidacion = erroresValidacion.filterKeys { it != "descripcion" }
                    },
                    label = { Text("Descripción") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(vertical = 4.dp),
                    isError = erroresValidacion.containsKey("descripcion"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (erroresValidacion.containsKey("descripcion")) Color.Red else ChocolateMedium,
                        unfocusedBorderColor = if (erroresValidacion.containsKey("descripcion")) Color.Red else ChocolateMedium,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = ChocolateDark,
                        unfocusedLabelColor = ChocolateDark,
                        errorLabelColor = Color.Red,
                        focusedTextColor = ChocolateDark,
                        unfocusedTextColor = ChocolateDark
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                erroresValidacion["descripcion"]?.let { error ->
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }
            
            // Campo Precio
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = precio,
                    onValueChange = { 
                        precio = it
                        erroresValidacion = erroresValidacion.filterKeys { it != "precio" }
                    },
                    label = { Text("Precio") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    isError = erroresValidacion.containsKey("precio"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (erroresValidacion.containsKey("precio")) Color.Red else ChocolateMedium,
                        unfocusedBorderColor = if (erroresValidacion.containsKey("precio")) Color.Red else ChocolateMedium,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = ChocolateDark,
                        unfocusedLabelColor = ChocolateDark,
                        errorLabelColor = Color.Red,
                        focusedTextColor = ChocolateDark,
                        unfocusedTextColor = ChocolateDark
                    )
                )
                erroresValidacion["precio"]?.let { error ->
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }
            
            // Campo Cantidad
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = cantidad,
                    onValueChange = { 
                        cantidad = it
                        erroresValidacion = erroresValidacion.filterKeys { it != "cantidad" }
                    },
                    label = { Text("Cantidad") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    isError = erroresValidacion.containsKey("cantidad"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (erroresValidacion.containsKey("cantidad")) Color.Red else ChocolateMedium,
                        unfocusedBorderColor = if (erroresValidacion.containsKey("cantidad")) Color.Red else ChocolateMedium,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = ChocolateDark,
                        unfocusedLabelColor = ChocolateDark,
                        errorLabelColor = Color.Red,
                        focusedTextColor = ChocolateDark,
                        unfocusedTextColor = ChocolateDark
                    )
                )
                erroresValidacion["cantidad"]?.let { error ->
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }
            
            // Imagen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(CreamDark, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
            ) {
                when {
                    imagenBitmap != null -> {
                        Image(
                            bitmap = imagenBitmap!!.asImageBitmap(),
                            contentDescription = "Foto del producto",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    rutaFoto != null && rutaFoto!!.startsWith("http") -> {
                        // Imagen desde URL (del catálogo web)
                        Image(
                            painter = rememberAsyncImagePainter(rutaFoto),
                            contentDescription = "Imagen del producto",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    else -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "📷",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Sin imagen",
                                color = ChocolateMedium.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
            
            // Botones de imagen en una fila armoniosa
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onSeleccionarImagen,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChocolateMedium
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "📷 Seleccionar Imagen",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Botón para eliminar imagen si existe
                if (imagenBitmap != null || (rutaFoto != null && rutaFoto!!.startsWith("http"))) {
                    Button(
                        onClick = {
                            imagenBitmap = null
                            rutaFoto = null
                            rutaImagenCapturada = null
                            imagenCapturada = null
                        },
                        modifier = Modifier
                            .height(56.dp)
                            .width(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red.copy(alpha = 0.8f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "✕",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            // Botón Guardar
            Button(
                onClick = {
                    scope.launch {
                        // Validar primero
                        val resultado = ProductoValidator.validarProducto(
                            id, nombre, descripcion, precio, cantidad,
                            esModoEdicion, productoViewModel
                        )
                        
                        // Mostrar errores si los hay
                        erroresValidacion = resultado.errores
                        
                        // Si hay errores, no continuar (NO mostrar Toast)
                        if (!resultado.esValido) {
                            return@launch
                        }
                        
                        // Si todo está bien, guardar
                        val exito = ProductoValidator.guardarProducto(
                            id, nombre, descripcion, precio, cantidad, rutaFoto,
                            esModoEdicion, productoViewModel
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
                            erroresValidacion = emptyMap()
                            // El mensaje de éxito se muestra desde el ViewModel (Toast solo para éxito)
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

