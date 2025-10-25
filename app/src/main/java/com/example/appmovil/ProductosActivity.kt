package com.example.appmovil

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory

class ProductosActivity : AppCompatActivity() {
    
    private lateinit var listViewProductos: ListView
    private lateinit var buttonVolver: Button
    private lateinit var productoViewModel: ProductoViewModel
    private lateinit var productoAdapter: ProductoAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos)
        
        initViews()
        setupViewModel()
        setupAdapter()
        setupClickListeners()
        observeViewModel()
    }
    
    private fun initViews() {
        listViewProductos = findViewById(R.id.listViewProductos)
        buttonVolver = findViewById(R.id.buttonVolver)
    }
    
    private fun setupViewModel() {
        productoViewModel = ViewModelProvider(this, AndroidViewModelFactory.getInstance(application))[ProductoViewModel::class.java]
    }
    
    private fun setupAdapter() {
        productoAdapter = ProductoAdapter(this, emptyList())
        listViewProductos.adapter = productoAdapter
    }
    
    private fun setupClickListeners() {
        buttonVolver.setOnClickListener {
            finish()
        }
        
        listViewProductos.setOnItemClickListener { _, _, position, _ ->
            val producto = productoAdapter.getItem(position)
            mostrarDialogoEliminar(producto)
        }
    }
    
    private fun observeViewModel() {
        productoViewModel.allProductos.observe(this) { productos ->
            productoAdapter.actualizarProductos(productos)
        }
        
        productoViewModel.mensaje.observe(this) { mensaje ->
            if (mensaje.isNotEmpty()) {
                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
                productoViewModel.limpiarMensaje()
            }
        }
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
