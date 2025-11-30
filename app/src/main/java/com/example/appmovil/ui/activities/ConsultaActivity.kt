package com.example.appmovil.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import com.example.appmovil.data.Producto
import com.example.appmovil.ui.screens.ConsultaScreen
import com.example.appmovil.ui.theme.AppMovilTheme
import com.example.appmovil.ui.viewmodel.ProductoViewModel

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
                    productoViewModel = productoViewModel
                )
            }
        }
    }
    
    private fun setupViewModel() {
        productoViewModel = ViewModelProvider(this, AndroidViewModelFactory.getInstance(application))[ProductoViewModel::class.java]
    }
    
    private fun mostrarDetallesProducto(producto: Producto) {
        val intent = Intent(this, DetalleProductoActivity::class.java)
        intent.putExtra("producto", producto)
        startActivity(intent)
    }
}

