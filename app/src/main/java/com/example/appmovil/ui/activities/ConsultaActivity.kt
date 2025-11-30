package com.example.appmovil.ui.activities

import android.app.AlertDialog
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
import com.example.appmovil.data.Producto
import com.example.appmovil.ui.screens.ConsultaScreen
import com.example.appmovil.ui.theme.AppMovilTheme
import com.example.appmovil.ui.viewmodel.ProductoViewModel
import kotlinx.coroutines.launch

class ConsultaActivity : ComponentActivity() {
    
    private lateinit var productoViewModel: ProductoViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setupViewModel()
        
        setContent {
            AppMovilTheme {
                ConsultaScreen(
                    onVolverClick = { finish() },
                    onProductoClick = { producto ->
                        mostrarDetallesProducto(producto)
                    },
                    onMensaje = { mensaje ->
                        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
                    },
                    onEliminarPorId = { id ->
                        eliminarProductoPorId(id)
                    },
                    productoViewModel = productoViewModel
                )
            }
        }
    }
    
    private fun setupViewModel() {
        productoViewModel = ViewModelProvider(this, AndroidViewModelFactory.getInstance(application))[ProductoViewModel::class.java]
    }
    
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
    
    private fun mostrarDetallesProducto(producto: Producto) {
        val intent = Intent(this, DetalleProductoActivity::class.java)
        intent.putExtra("producto", producto)
        startActivity(intent)
        overridePendingTransition(R.anim.scale_in, R.anim.fade_out)
    }
    
    private fun eliminarProductoPorId(id: String) {
        lifecycleScope.launch {
            try {
                val producto = productoViewModel.getProductoByIdSync(id)
                if (producto == null) {
                    Toast.makeText(this@ConsultaActivity, "Producto con ID '$id' no encontrado", Toast.LENGTH_LONG).show()
                    return@launch
                }
                
                AlertDialog.Builder(this@ConsultaActivity)
                    .setTitle("Eliminar Producto")
                    .setMessage("¿Estás seguro de que quieres eliminar el producto '${producto.nombre}' (ID: ${producto.id})?\n\nEsta acción no se puede deshacer.")
                    .setPositiveButton("Eliminar") { _, _ ->
                        productoViewModel.eliminarProducto(producto)
                        Toast.makeText(this@ConsultaActivity, "Producto eliminado exitosamente", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            } catch (e: Exception) {
                Toast.makeText(this@ConsultaActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}

