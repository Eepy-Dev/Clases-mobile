package com.example.appmovil.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.appmovil.data.Producto
import com.example.appmovil.ui.screens.DetalleProductoScreen
import com.example.appmovil.ui.theme.AppMovilTheme

class DetalleProductoActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
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
                    }
                )
            }
        }
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
}

