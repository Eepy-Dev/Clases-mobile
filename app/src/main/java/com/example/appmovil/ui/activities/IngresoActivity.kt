package com.example.appmovil.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import com.example.appmovil.R
import com.example.appmovil.data.Producto
import com.example.appmovil.ui.screens.IngresoScreen
import com.example.appmovil.ui.theme.AppMovilTheme
import com.example.appmovil.ui.viewmodel.ProductoViewModel
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
                if (rutaFoto.isNotEmpty()) {
                    productoViewModel.setImagenCapturada(bitmap, rutaFoto)
                }
            }
        }
    }
    
    private val galeriaLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            uri?.let {
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                    val rutaFoto = guardarImagen(bitmap)
                    if (rutaFoto.isNotEmpty()) {
                        productoViewModel.setImagenCapturada(bitmap, rutaFoto)
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al cargar imagen: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private var mostrarDialogoSeleccionImagen: (() -> Unit)? = null
    
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
                    onSeleccionarImagen = {
                        mostrarDialogoSeleccionarImagen()
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
        // Verificar si viene un producto del catálogo online
        val producto = intent.getSerializableExtra("producto") as? Producto
        if (producto != null) {
            esModoEdicion = false // No es edición, es un nuevo producto desde catálogo
            productoEditando = producto
            return
        }
        
        // Verificar si es modo edición normal
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
    
    private fun mostrarDialogoSeleccionarImagen() {
        val opciones = arrayOf("Cámara", "Galería")
        android.app.AlertDialog.Builder(this)
            .setTitle("Seleccionar imagen")
            .setItems(opciones) { _, cual ->
                when (cual) {
                    0 -> solicitarPermisosYCamara()
                    1 -> abrirGaleria()
                }
            }
            .show()
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
        if (intent.resolveActivity(packageManager) != null) {
            cameraLauncher.launch(intent)
        } else {
            Toast.makeText(this, "No se puede abrir la cámara", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galeriaLauncher.launch(intent)
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
    
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}

