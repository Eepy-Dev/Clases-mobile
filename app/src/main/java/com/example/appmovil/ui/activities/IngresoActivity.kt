package com.example.appmovil.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import com.example.appmovil.data.local.entity.Producto
import com.example.appmovil.ui.viewmodel.ProductoViewModel
import com.example.appmovil.ui.theme.AppMovilTheme
import com.example.appmovil.ui.theme.ChocolateDark
import com.example.appmovil.ui.theme.ChocolateMedium
import com.example.appmovil.ui.theme.Cream
import com.example.appmovil.ui.theme.CreamDark
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class IngresoActivity : ComponentActivity() {
    
    private lateinit var productoViewModel: ProductoViewModel
    private var esModoEdicion = false
    private var productoEditando: Producto? = null
    private var imageUri: Uri? = null
    
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri?.let { uri ->
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    val rutaFoto = guardarImagen(bitmap)
                    if (rutaFoto.isNotEmpty()) {
                        productoViewModel.setImagenCapturada(bitmap, rutaFoto)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } ?: run {
                val bitmap = result.data?.extras?.get("data") as? Bitmap
                bitmap?.let {
                    val rutaFoto = guardarImagen(it)
                    if (rutaFoto.isNotEmpty()) {
                        productoViewModel.setImagenCapturada(it, rutaFoto)
                    }
                }
            }
        }
    }
    
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    val rutaFoto = guardarImagen(bitmap)
                    if (rutaFoto.isNotEmpty()) {
                        productoViewModel.setImagenCapturada(bitmap, rutaFoto)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setupViewModel()
        verificarModoEdicion()
        
        setContent {
            AppMovilTheme {
                IngresoScreen(
                    onVolverClick = { finish() },
                    onProductoGuardado = { mensaje ->
                        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
                        finish()
                    },
                    onSeleccionarImagen = { esCamara ->
                        if (esCamara) {
                            solicitarPermisosYCamara()
                        } else {
                            solicitarPermisosYGalería()
                        }
                    },
                    productoViewModel = productoViewModel,
                    esModoEdicion = esModoEdicion,
                    productoEditando = productoEditando
                )
            }
        }
    }
    
    private fun setupViewModel() {
        productoViewModel = ViewModelProvider(this, AndroidViewModelFactory.getInstance(application))[ProductoViewModel::class.java]
    }
    
    private var productoDesdeCatalogo = false // Indica si el producto viene del catálogo web (backend)
    
    private fun verificarModoEdicion() {
        val producto = intent.getSerializableExtra("producto") as? Producto
        val productoId = intent.getStringExtra("producto_id")
        val productoExterno = intent.getSerializableExtra("producto_externo") as? Producto
        
        if (producto != null) {
            // Verificar si viene del catálogo web (tiene ID numérico, no PROD- ni EXT-)
            val esDelCatalogo = !producto.id.startsWith("PROD-") && 
                               !producto.id.startsWith("EXT-") && 
                               producto.id.toLongOrNull() != null
            
            if (esDelCatalogo) {
                // Producto del catálogo web (backend) - modo creación, no sincronizar
                productoDesdeCatalogo = true
                productoEditando = producto
            } else {
                // Producto pasado directamente (desde DetalleProductoActivity o similar) - modo edición
                esModoEdicion = true
                productoEditando = producto
            }
            
            if (producto.foto != null && producto.foto.startsWith("http")) {
                descargarImagen(producto.foto)
            }
        } else if (productoId != null) {
            // Solo se pasa el ID, buscar el producto
            esModoEdicion = true
            productoViewModel.getProductoById(productoId)
        } else if (productoExterno != null) {
            // Producto desde catálogo externo antiguo (EXT-) - modo creación, no edición
            productoEditando = productoExterno
            if (productoExterno.foto != null && productoExterno.foto.startsWith("http")) {
                descargarImagen(productoExterno.foto)
            }
        }
    }
    
    private fun descargarImagen(urlString: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                connection.connect()
                
                val inputStream = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
                connection.disconnect()
                
                bitmap?.let {
                    withContext(Dispatchers.Main) {
                        val rutaFoto = guardarImagen(it)
                        if (rutaFoto.isNotEmpty()) {
                            productoViewModel.setImagenCapturada(it, rutaFoto)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            abrirCamara()
        } else {
            Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun solicitarPermisosYCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            abrirCamara()
        }
    }
    
    private fun abrirCamara() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            val photoFile = File(getExternalFilesDir(null), "temp_camera_${System.currentTimeMillis()}.jpg")
            imageUri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                photoFile
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            if (intent.resolveActivity(packageManager) != null) {
                cameraLauncher.launch(intent)
            } else {
                Toast.makeText(this, "No se puede abrir la cámara", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Fallback sin FileProvider
            if (intent.resolveActivity(packageManager) != null) {
                cameraLauncher.launch(intent)
            } else {
                Toast.makeText(this, "No se puede abrir la cámara", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private val requestStoragePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            abrirGaleria()
        } else {
            Toast.makeText(this, "Permiso de galería denegado", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun solicitarPermisosYGalería() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ usa READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) 
                != PackageManager.PERMISSION_GRANTED) {
                requestStoragePermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                abrirGaleria()
            }
        } else {
            // Android 12 y anteriores usan READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) 
                != PackageManager.PERMISSION_GRANTED) {
                requestStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                abrirGaleria()
            }
        }
    }
    
    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(packageManager) != null) {
            galleryLauncher.launch(intent)
        } else {
            Toast.makeText(this, "No se puede abrir la galería", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun guardarImagen(bitmap: Bitmap): String {
        val archivo = File(getExternalFilesDir(null), "producto_${System.currentTimeMillis()}.jpg")
        try {
            val fos = FileOutputStream(archivo)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
            fos.close()
            return archivo.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            return ""
        }
    }
}

@Composable
fun IngresoScreen(
    onVolverClick: () -> Unit,
    onProductoGuardado: (String) -> Unit,
    onSeleccionarImagen: (Boolean) -> Unit, // true = cámara, false = galería
    productoViewModel: ProductoViewModel,
    esModoEdicion: Boolean,
    productoEditando: Producto?
) {
    // Generar ID automático para productos nuevos (no editando, no externos)
    val idGenerado = remember(esModoEdicion, productoEditando) {
        if (!esModoEdicion && productoEditando == null) {
            "PROD-${System.currentTimeMillis()}"
        } else {
            ""
        }
    }
    
    var id by remember { mutableStateOf(idGenerado) }
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
    var esProductoExterno by remember { mutableStateOf(false) } // Para productos EXT-
    val coroutineScope = rememberCoroutineScope()
    
    // Estados de error para validación visual
    var errorId by remember { mutableStateOf("") }
    var errorNombre by remember { mutableStateOf("") }
    var errorDescripcion by remember { mutableStateOf("") }
    var errorPrecio by remember { mutableStateOf("") }
    var errorCantidad by remember { mutableStateOf("") }
    
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
    
    LaunchedEffect(productoSeleccionado, productoEditando) {
        val producto = productoSeleccionado ?: productoEditando
        producto?.let {
            // Siempre usar el ID del producto (no generar uno nuevo en modo edición)
            id = it.id
            nombre = it.nombre
            descripcion = it.descripcion
            precio = it.precio.toString()
            cantidad = it.cantidad.toString()
            rutaFoto = it.foto
            // Verificar si es producto externo (EXT-)
            esProductoExterno = it.id.startsWith("EXT-")
            if (it.foto != null) {
                if (it.foto.startsWith("http")) {
                    // Es una URL, se mostrará con AsyncImage y se descargará automáticamente
                } else {
                    val bitmap = cargarImagen(it.foto)
                    if (bitmap != null) {
                        imagenBitmap = bitmap
                    }
                }
            }
        }
    }
    
    LaunchedEffect(mensaje) {
        if (mensaje.isNotEmpty() && (mensaje.contains("exitosamente") || mensaje.contains("guardado"))) {
            val mensajeFinal = if (esModoEdicion) "Producto actualizado exitosamente" else "Producto añadido exitosamente"
            onProductoGuardado(mensajeFinal)
            productoViewModel.limpiarMensaje()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .verticalScroll(rememberScrollState())
    ) {
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Campo ID (NUNCA editable)
            OutlinedTextField(
                value = id,
                onValueChange = { }, // No permitir cambios
                label = { Text("ID del Producto") },
                singleLine = true,
                enabled = false, // Siempre deshabilitado
                readOnly = true, // Solo lectura
                modifier = Modifier.fillMaxWidth(),
                isError = errorId.isNotEmpty(),
                supportingText = if (errorId.isNotEmpty()) {
                    { Text(errorId, color = Color.Red, fontSize = 12.sp) }
                } else null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (errorId.isEmpty()) ChocolateMedium else Color.Red,
                    unfocusedBorderColor = if (errorId.isEmpty()) ChocolateMedium else Color.Red,
                    focusedLabelColor = ChocolateDark,
                    unfocusedLabelColor = ChocolateDark,
                    focusedTextColor = ChocolateDark,
                    unfocusedTextColor = ChocolateDark
                )
            )
            
            // Campo Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = { 
                    nombre = it
                    errorNombre = ""
                },
                label = { Text("Nombre del Producto") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = errorNombre.isNotEmpty(),
                supportingText = if (errorNombre.isNotEmpty()) {
                    { Text(errorNombre, color = Color.Red, fontSize = 12.sp) }
                } else null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (errorNombre.isEmpty()) ChocolateMedium else Color.Red,
                    unfocusedBorderColor = if (errorNombre.isEmpty()) ChocolateMedium else Color.Red,
                    focusedLabelColor = ChocolateDark,
                    unfocusedLabelColor = ChocolateDark,
                    focusedTextColor = ChocolateDark,
                    unfocusedTextColor = ChocolateDark
                )
            )
            
            // Campo Descripción
            OutlinedTextField(
                value = descripcion,
                onValueChange = { 
                    descripcion = it
                    errorDescripcion = ""
                },
                label = { Text("Descripción") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                isError = errorDescripcion.isNotEmpty(),
                supportingText = if (errorDescripcion.isNotEmpty()) {
                    { Text(errorDescripcion, color = Color.Red, fontSize = 12.sp) }
                } else null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (errorDescripcion.isEmpty()) ChocolateMedium else Color.Red,
                    unfocusedBorderColor = if (errorDescripcion.isEmpty()) ChocolateMedium else Color.Red,
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
                onValueChange = { 
                    precio = it
                    errorPrecio = ""
                },
                label = { Text("Precio") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = errorPrecio.isNotEmpty(),
                supportingText = if (errorPrecio.isNotEmpty()) {
                    { Text(errorPrecio, color = Color.Red, fontSize = 12.sp) }
                } else null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (errorPrecio.isEmpty()) ChocolateMedium else Color.Red,
                    unfocusedBorderColor = if (errorPrecio.isEmpty()) ChocolateMedium else Color.Red,
                    focusedLabelColor = ChocolateDark,
                    unfocusedLabelColor = ChocolateDark,
                    focusedTextColor = ChocolateDark,
                    unfocusedTextColor = ChocolateDark
                )
            )
            
            // Campo Cantidad
            OutlinedTextField(
                value = cantidad,
                onValueChange = { 
                    cantidad = it
                    errorCantidad = ""
                },
                label = { Text("Cantidad") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = errorCantidad.isNotEmpty(),
                supportingText = if (errorCantidad.isNotEmpty()) {
                    { Text(errorCantidad, color = Color.Red, fontSize = 12.sp) }
                } else null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (errorCantidad.isEmpty()) ChocolateMedium else Color.Red,
                    unfocusedBorderColor = if (errorCantidad.isEmpty()) ChocolateMedium else Color.Red,
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
                when {
                    imagenBitmap != null -> {
                        Image(
                            bitmap = imagenBitmap!!.asImageBitmap(),
                            contentDescription = "Foto del producto",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    rutaFoto != null && rutaFoto?.startsWith("http") == true -> {
                        AsyncImage(
                            model = rutaFoto,
                            contentDescription = "Imagen del producto",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    else -> {
                        Image(
                            painter = painterResource(id = android.R.drawable.ic_menu_camera),
                            contentDescription = "Tomar foto",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        )
                    }
                }
            }
            
            // Botones de selección de imagen
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onSeleccionarImagen(true) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChocolateMedium
                    )
                ) {
                    Text("Cámara", color = Color.White)
                }
                
                Button(
                    onClick = { onSeleccionarImagen(false) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChocolateMedium
                    )
                ) {
                    Text("Galería", color = Color.White)
                }
            }
            
            // Botón Guardar
            Button(
                onClick = {
                    coroutineScope.launch {
                        // Usar la ruta de imagen descargada si está disponible, sino usar rutaFoto
                        val rutaFinal = rutaImagenCapturada ?: rutaFoto
                        val resultado = guardarProductoConValidacion(
                            id, nombre, descripcion, precio, cantidad, rutaFinal,
                            esModoEdicion, productoViewModel
                        ) { errores ->
                            errorId = errores.errorId
                            errorNombre = errores.errorNombre
                            errorDescripcion = errores.errorDescripcion
                            errorPrecio = errores.errorPrecio
                            errorCantidad = errores.errorCantidad
                        }
                        if (resultado.exito) {
                            // El mensaje se manejará automáticamente desde el ViewModel
                            // y cerrará la actividad
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

data class ErroresValidacion(
    val errorId: String,
    val errorNombre: String,
    val errorDescripcion: String,
    val errorPrecio: String,
    val errorCantidad: String
)

data class ResultadoGuardado(val exito: Boolean)

private suspend fun guardarProductoConValidacion(
    id: String,
    nombre: String,
    descripcion: String,
    precio: String,
    cantidad: String,
    rutaFoto: String?,
    esModoEdicion: Boolean,
    productoViewModel: ProductoViewModel,
    onErrores: (ErroresValidacion) -> Unit
): ResultadoGuardado {
    var errorId = ""
    var errorNombre = ""
    var errorDescripcion = ""
    var errorPrecio = ""
    var errorCantidad = ""
    
    // Asegurar que el ID no esté vacío (generar solo si no hay ID y no es edición)
    // Usar ID corto PROD-1, PROD-2, etc.
    val idFinal = if (id.trim().isEmpty() && !esModoEdicion) {
        productoViewModel.generarSiguienteIdProd()
    } else {
        id.trim()
    }
    
    // Validaciones
    if (idFinal.isEmpty()) {
        errorId = "El ID es obligatorio"
    }
    if (nombre.trim().isEmpty()) {
        errorNombre = "El nombre es obligatorio"
    }
    if (descripcion.trim().isEmpty()) {
        errorDescripcion = "La descripción es obligatoria"
    }
    if (precio.trim().isEmpty()) {
        errorPrecio = "El precio es obligatorio"
    } else {
        val precioDouble = precio.toDoubleOrNull()
        if (precioDouble == null || precioDouble <= 0) {
            errorPrecio = "El precio debe ser un número válido mayor a 0"
        }
    }
    if (cantidad.trim().isEmpty()) {
        errorCantidad = "La cantidad es obligatoria"
    } else {
        val cantidadInt = cantidad.toIntOrNull()
        if (cantidadInt == null || cantidadInt < 0) {
            errorCantidad = "La cantidad debe ser un número válido mayor o igual a 0"
        }
    }
    
    if (errorId.isNotEmpty() || errorNombre.isNotEmpty() || errorDescripcion.isNotEmpty() || 
        errorPrecio.isNotEmpty() || errorCantidad.isNotEmpty()) {
        onErrores(ErroresValidacion(errorId, errorNombre, errorDescripcion, errorPrecio, errorCantidad))
        return ResultadoGuardado(false)
    }
    
    val precioDouble = precio.toDoubleOrNull() ?: 0.0
    val cantidadInt = cantidad.toIntOrNull() ?: 0
    
    val producto = Producto(
        id = idFinal,
        nombre = nombre.trim(),
        descripcion = descripcion.trim(),
        precio = precioDouble,
        cantidad = cantidadInt,
        foto = rutaFoto
    )
    
    if (esModoEdicion) {
        // En modo edición, SIEMPRE actualizar (PUT), nunca insertar
        productoViewModel.actualizarProducto(producto)
    } else {
        // Solo insertar si no es edición
        // Si viene del catálogo web (backend), no sincronizar porque ya existe en el backend
        // Un producto viene del catálogo si tiene ID numérico (no PROD- ni EXT-)
        val esDelCatalogo = !idFinal.startsWith("PROD-") && 
                           !idFinal.startsWith("EXT-") && 
                           idFinal.toLongOrNull() != null
        val sincronizar = !esDelCatalogo
        productoViewModel.insertarProducto(producto, sincronizar)
    }
    
    return ResultadoGuardado(true)
}

private fun cargarImagen(ruta: String): Bitmap? {
    val archivo = File(ruta)
    return if (archivo.exists()) {
        BitmapFactory.decodeFile(ruta)
    } else {
        null
    }
}
