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
import com.example.appmovil.data.ProductoExterno
import com.example.appmovil.ui.screens.ProductosExternosScreen
import com.example.appmovil.ui.theme.AppMovilTheme
import com.example.appmovil.ui.viewmodel.ProductoViewModel

class ProductosExternosActivity : ComponentActivity() {
    
    private lateinit var productoViewModel: ProductoViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setupViewModel()
        
        setContent {
            AppMovilTheme {
                ProductosExternosScreen(
                    productoViewModel = productoViewModel,
                    onBackClick = {
                        finish()
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    },
                    onAgregarAlInventario = { productoExterno ->
                        agregarProductoAlInventario(productoExterno)
                    }
                )
            }
        }
    }
    
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
    
    private fun setupViewModel() {
        productoViewModel = ViewModelProvider(
            this,
            AndroidViewModelFactory.getInstance(application)
        )[ProductoViewModel::class.java]
    }
    
    private fun agregarProductoAlInventario(productoExterno: ProductoExterno) {
        val productoLocal = productoViewModel.convertirProductoExternoALocal(productoExterno)
        
        // Navegar a IngresoActivity en modo edición con los datos prellenados
        val intent = Intent(this, IngresoActivity::class.java).apply {
            putExtra("producto", productoLocal)
            putExtra("esModoEdicion", false) // Es un nuevo producto desde la API
        }
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}

