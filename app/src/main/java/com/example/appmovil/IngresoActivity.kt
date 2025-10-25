package com.example.appmovil

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import com.example.appmovil.ui.theme.AppMovilTheme
import com.example.appmovil.ui.theme.ChocolateDark
import com.example.appmovil.ui.theme.ChocolateMedium
import com.example.appmovil.ui.theme.Cream
import com.example.appmovil.ui.theme.CreamDark
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class IngresoActivity : ComponentActivity() {
    
    private lateinit var productoViewModel: ProductoViewModel
    private var esModoEdicion = false
    private var productoEditando: Producto? = null
    
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val bitmap = result.data?.extras?.get("data") as? Bitmap
            bitmap?.let {
                val rutaFoto = guardarImagen(it)
                // Aquí necesitaríamos pasar la imagen al composable
                // Por simplicidad, manejaremos esto en el composable
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
                    onMensaje = { mensaje ->
                        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
                    },
                    onTomarFoto = {
                        solicitarPermisosYCamara()
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
    
    private fun verificarModoEdicion() {
        val productoId = intent.getStringExtra("producto_id")
        if (productoId != null) {
            esModoEdicion = true
            productoViewModel.getProductoById(productoId)
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
            // Solicitar permiso de cámara
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            abrirCamara()
        }
    }
    
    private fun abrirCamara() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            cameraLauncher.launch(intent)
        } else {
            Toast.makeText(this, "No se puede abrir la cámara", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun guardarImagen(bitmap: Bitmap): String {
        val archivo = File(getExternalFilesDir(null), "producto_${System.currentTimeMillis()}.jpg")
        try {
            val fos = FileOutputStream(archivo)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
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
    
    DisposableEffect(Unit) {
        val productoObserver = androidx.lifecycle.Observer<Producto?> { producto ->
            productoSeleccionado = producto
        }
        val mensajeObserver = androidx.lifecycle.Observer<String> { msg ->
            mensaje = msg
        }
        productoViewModel.productoSeleccionado.observeForever(productoObserver)
        productoViewModel.mensaje.observeForever(mensajeObserver)
        onDispose {
            productoViewModel.productoSeleccionado.removeObserver(productoObserver)
            productoViewModel.mensaje.removeObserver(mensajeObserver)
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
                val bitmap = cargarImagen(producto.foto)
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Campo ID
            OutlinedTextField(
                value = id,
                onValueChange = { id = it },
                label = { Text("ID del Producto") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
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
                modifier = Modifier.fillMaxWidth(),
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
                    .height(100.dp),
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
                modifier = Modifier.fillMaxWidth(),
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
                modifier = Modifier.fillMaxWidth(),
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
                    guardarProducto(
                        id, nombre, descripcion, precio, cantidad, rutaFoto,
                        esModoEdicion, productoViewModel, onMensaje
                    )
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

private fun guardarProducto(
    id: String,
    nombre: String,
    descripcion: String,
    precio: String,
    cantidad: String,
    rutaFoto: String?,
    esModoEdicion: Boolean,
    productoViewModel: ProductoViewModel,
    onMensaje: (String) -> Unit
) {
        // Validaciones
    if (id.trim().isEmpty()) {
        onMensaje("El ID es obligatorio")
            return
        }
    if (nombre.trim().isEmpty()) {
        onMensaje("El nombre es obligatorio")
            return
        }
    if (descripcion.trim().isEmpty()) {
        onMensaje("La descripción es obligatoria")
            return
        }
    if (precio.trim().isEmpty()) {
        onMensaje("El precio es obligatorio")
            return
        }
    if (cantidad.trim().isEmpty()) {
        onMensaje("La cantidad es obligatoria")
            return
        }
        
    val precioDouble = precio.toDoubleOrNull()
    val cantidadInt = cantidad.toIntOrNull()
        
    if (precioDouble == null || precioDouble <= 0) {
        onMensaje("El precio debe ser un número válido mayor a 0")
            return
        }
    if (cantidadInt == null || cantidadInt < 0) {
        onMensaje("La cantidad debe ser un número válido mayor o igual a 0")
            return
        }
        
        val producto = Producto(
        id = id.trim(),
        nombre = nombre.trim(),
        descripcion = descripcion.trim(),
        precio = precioDouble,
        cantidad = cantidadInt,
            foto = rutaFoto
        )
        
        if (esModoEdicion) {
            productoViewModel.actualizarProducto(producto)
        } else {
            productoViewModel.insertarProducto(producto)
        }
}

private fun cargarImagen(ruta: String): Bitmap? {
    val archivo = File(ruta)
    return if (archivo.exists()) {
        BitmapFactory.decodeFile(ruta)
    } else {
        null
    }
}