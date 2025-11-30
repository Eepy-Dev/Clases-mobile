package com.example.appmovil.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import com.example.appmovil.R
import com.example.appmovil.data.Producto
import com.example.appmovil.ui.screens.DetalleProductoScreen
import com.example.appmovil.ui.theme.AppMovilTheme
import com.example.appmovil.ui.viewmodel.ProductoViewModel

class DetalleProductoActivity : ComponentActivity() {
    
    private lateinit var productoViewModel: ProductoViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setupViewModel()
        
        val producto = intent.getSerializableExtra("producto") as? Producto
        
        if (producto == null) {
            Toast.makeText(this, "Error: Producto no encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        setContent {
            AppMovilTheme {
                DetalleProductoScreen(
                    producto = producto,
                    onVolverClick = { finish() },
                    onCompartirClick = { producto ->
                        compartirProducto(producto)
                    },
                    onEditarClick = { producto ->
                        val intent = Intent(this, IngresoActivity::class.java)
                        intent.putExtra("producto_id", producto.id)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                        finish()
                    },
                    onEliminarClick = { producto ->
                        productoViewModel.eliminarProducto(producto)
                        finish()
                    },
                    onMensaje = { mensaje ->
                        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
                    },
                    productoViewModel = productoViewModel
                )
            }
        }
    }
    
    private fun setupViewModel() {
        productoViewModel = ViewModelProvider(this, AndroidViewModelFactory.getInstance(application))[ProductoViewModel::class.java]
    }
    
    private fun compartirProducto(producto: Producto) {
        val mensaje = """
            🍫 Detalles del Producto:
            
            📋 ID: ${producto.id}
            🏷️ Nombre: ${producto.nombre}
            📝 Descripción: ${producto.descripcion}
            💰 Precio: $${producto.precio}
            📦 Stock: ${producto.cantidad}
        """.trimIndent()
        
        try {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.setPackage("com.whatsapp")
            intent.putExtra(Intent.EXTRA_TEXT, mensaje)
            startActivity(intent)
        } catch (e: Exception) {
            // Si WhatsApp no está instalado, usar cualquier app de mensajería
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, mensaje)
            startActivity(Intent.createChooser(intent, "Compartir producto"))
        }
    }
    
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fade_in, R.anim.scale_out)
    }
}

