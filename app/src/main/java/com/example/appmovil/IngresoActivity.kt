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
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class IngresoActivity : AppCompatActivity() {
    
    private lateinit var editTextId: EditText
    private lateinit var editTextNombre: EditText
    private lateinit var editTextDescripcion: EditText
    private lateinit var editTextPrecio: EditText
    private lateinit var editTextCantidad: EditText
    private lateinit var imageViewFoto: ImageView
    private lateinit var buttonTomarFoto: Button
    private lateinit var buttonGuardar: Button
    private lateinit var buttonVolver: Button
    
    private lateinit var productoViewModel: ProductoViewModel
    private var rutaFoto: String? = null
    private var esModoEdicion = false
    private var productoEditando: Producto? = null
    
    companion object {
        private const val REQUEST_CAMERA = 1
        private const val REQUEST_PERMISSION_CAMERA = 2
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingreso)
        
        initViews()
        setupViewModel()
        setupClickListeners()
        observeViewModel()
        verificarModoEdicion()
    }
    
    private fun initViews() {
        editTextId = findViewById(R.id.editTextId)
        editTextNombre = findViewById(R.id.editTextNombre)
        editTextDescripcion = findViewById(R.id.editTextDescripcion)
        editTextPrecio = findViewById(R.id.editTextPrecio)
        editTextCantidad = findViewById(R.id.editTextCantidad)
        imageViewFoto = findViewById(R.id.imageViewFoto)
        buttonTomarFoto = findViewById(R.id.buttonTomarFoto)
        buttonGuardar = findViewById(R.id.buttonGuardar)
        buttonVolver = findViewById(R.id.buttonVolver)
    }
    
    private fun setupViewModel() {
        productoViewModel = ViewModelProvider(this, AndroidViewModelFactory.getInstance(application))[ProductoViewModel::class.java]
    }
    
    private fun setupClickListeners() {
        buttonTomarFoto.setOnClickListener {
            solicitarPermisosYCamara()
        }
        
        buttonGuardar.setOnClickListener {
            guardarProducto()
        }
        
        buttonVolver.setOnClickListener {
            finish()
        }
    }
    
    private fun observeViewModel() {
        productoViewModel.productoSeleccionado.observe(this) { producto ->
            producto?.let {
                cargarDatosProducto(it)
            }
        }
        
        productoViewModel.mensaje.observe(this) { mensaje ->
            if (mensaje.isNotEmpty()) {
                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
                productoViewModel.limpiarMensaje()
            }
        }
    }
    
    private fun verificarModoEdicion() {
        val productoId = intent.getStringExtra("producto_id")
        if (productoId != null) {
            esModoEdicion = true
            productoViewModel.getProductoById(productoId)
        }
    }
    
    private fun cargarDatosProducto(producto: Producto) {
        productoEditando = producto
        editTextId.setText(producto.id)
        editTextNombre.setText(producto.nombre)
        editTextDescripcion.setText(producto.descripcion)
        editTextPrecio.setText(producto.precio.toString())
        editTextCantidad.setText(producto.cantidad.toString())
        
        if (producto.foto != null) {
            rutaFoto = producto.foto
            cargarImagen(producto.foto)
        }
    }
    
    private fun solicitarPermisosYCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_PERMISSION_CAMERA
            )
        } else {
            abrirCamara()
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirCamara()
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun abrirCamara() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_CAMERA)
        } else {
            Toast.makeText(this, "No se puede abrir la cámara", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            val bitmap = data?.extras?.get("data") as? Bitmap
            bitmap?.let {
                rutaFoto = guardarImagen(it)
                imageViewFoto.setImageBitmap(it)
            }
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
    
    private fun cargarImagen(ruta: String) {
        val archivo = File(ruta)
        if (archivo.exists()) {
            val bitmap = BitmapFactory.decodeFile(ruta)
            imageViewFoto.setImageBitmap(bitmap)
        }
    }
    
    private fun guardarProducto() {
        val id = editTextId.text.toString().trim()
        val nombre = editTextNombre.text.toString().trim()
        val descripcion = editTextDescripcion.text.toString().trim()
        val precioStr = editTextPrecio.text.toString().trim()
        val cantidadStr = editTextCantidad.text.toString().trim()
        
        // Validaciones
        if (id.isEmpty()) {
            Toast.makeText(this, "El ID es obligatorio", Toast.LENGTH_SHORT).show()
            return
        }
        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
            return
        }
        if (descripcion.isEmpty()) {
            Toast.makeText(this, "La descripción es obligatoria", Toast.LENGTH_SHORT).show()
            return
        }
        if (precioStr.isEmpty()) {
            Toast.makeText(this, "El precio es obligatorio", Toast.LENGTH_SHORT).show()
            return
        }
        if (cantidadStr.isEmpty()) {
            Toast.makeText(this, "La cantidad es obligatoria", Toast.LENGTH_SHORT).show()
            return
        }
        
        val precio = precioStr.toDoubleOrNull()
        val cantidad = cantidadStr.toIntOrNull()
        
        if (precio == null || precio <= 0) {
            Toast.makeText(this, "El precio debe ser un número válido mayor a 0", Toast.LENGTH_SHORT).show()
            return
        }
        if (cantidad == null || cantidad < 0) {
            Toast.makeText(this, "La cantidad debe ser un número válido mayor o igual a 0", Toast.LENGTH_SHORT).show()
            return
        }
        
        val producto = Producto(
            id = id,
            nombre = nombre,
            descripcion = descripcion,
            precio = precio,
            cantidad = cantidad,
            foto = rutaFoto
        )
        
        if (esModoEdicion) {
            productoViewModel.actualizarProducto(producto)
        } else {
            productoViewModel.insertarProducto(producto)
        }
        
        // Limpiar formulario después de guardar
        if (!esModoEdicion) {
            limpiarFormulario()
        }
    }
    
    private fun limpiarFormulario() {
        editTextId.text.clear()
        editTextNombre.text.clear()
        editTextDescripcion.text.clear()
        editTextPrecio.text.clear()
        editTextCantidad.text.clear()
        imageViewFoto.setImageResource(android.R.drawable.ic_menu_camera)
        rutaFoto = null
    }
}
