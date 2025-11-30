package com.example.appmovil.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.lifecycleScope
import com.example.appmovil.R
import com.example.appmovil.ui.screens.HistorialScreen
import com.example.appmovil.ui.theme.AppMovilTheme
import com.example.appmovil.ui.viewmodel.ProductoViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HistorialActivity : ComponentActivity() {
    
    private lateinit var productoViewModel: ProductoViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setupViewModel()
        
        setContent {
            AppMovilTheme {
                HistorialScreen(
                    onVolverClick = { finish() },
                    onCompartirHistorial = {
                        compartirHistorial()
                    },
                    productoViewModel = productoViewModel
                )
            }
        }
    }
    
    private fun setupViewModel() {
        productoViewModel = ViewModelProvider(this, AndroidViewModelFactory.getInstance(application))[ProductoViewModel::class.java]
    }
    
    private fun compartirHistorial() {
        lifecycleScope.launch {
            try {
                val ultimosMovimientos = productoViewModel.getUltimosMovimientos(5)
                
                if (ultimosMovimientos.isEmpty()) {
                    Toast.makeText(this@HistorialActivity, "No hay movimientos para compartir", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                
                val fechaFormateada = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                val mensaje = StringBuilder()
                mensaje.append("📊 HISTORIAL DE MOVIMIENTOS (Últimos 5)\n\n")
                
                ultimosMovimientos.forEach { movimiento ->
                    val fecha = fechaFormateada.format(Date(movimiento.fecha))
                    val icono = when (movimiento.tipo) {
                        "ENTRADA" -> "➕"
                        "SALIDA" -> "➖"
                        "VENTA" -> "💰"
                        else -> "📦"
                    }
                    
                    mensaje.append("$icono ${movimiento.tipo} - ${movimiento.nombreProducto}\n")
                    mensaje.append("📅 $fecha\n")
                    mensaje.append("Cantidad: ${if (movimiento.tipo == "ENTRADA") "+" else "-"}${movimiento.cantidad}\n")
                    mensaje.append("Stock: ${movimiento.cantidadAnterior} → ${movimiento.cantidadNueva}\n\n")
                }
                
                try {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/plain"
                    intent.setPackage("com.whatsapp")
                    intent.putExtra(Intent.EXTRA_TEXT, mensaje.toString())
                    startActivity(intent)
                } catch (e: Exception) {
                    // Si WhatsApp no está instalado, usar cualquier app de mensajería
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/plain"
                    intent.putExtra(Intent.EXTRA_TEXT, mensaje.toString())
                    startActivity(Intent.createChooser(intent, "Compartir historial"))
                }
            } catch (e: Exception) {
                Toast.makeText(this@HistorialActivity, "Error al compartir historial: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}

