package com.example.appmovil.ui.activities

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import com.example.appmovil.data.Producto
import com.example.appmovil.ui.screens.ProductosScreen
import com.example.appmovil.ui.theme.AppMovilTheme
import com.example.appmovil.ui.viewmodel.ProductoViewModel

class ProductosActivity : ComponentActivity() {
    
    private lateinit var productoViewModel: ProductoViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setupViewModel()
        
        setContent {
            AppMovilTheme {
                ProductosScreen(
                    onVolverClick = { finish() },
                    onProductoClick = { producto ->
                        mostrarDialogoEliminar(producto)
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

    private fun mostrarDialogoEliminar(producto: Producto) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Producto")
            .setMessage("¿Estás seguro de que quieres eliminar el producto '${producto.nombre}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                productoViewModel.eliminarProducto(producto)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}

