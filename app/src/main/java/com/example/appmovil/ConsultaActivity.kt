package com.example.appmovil

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory

class ConsultaActivity : AppCompatActivity() {
    
    private lateinit var editTextBusqueda: EditText
    private lateinit var buttonBuscar: Button
    private lateinit var listViewResultados: ListView
    private lateinit var buttonVolver: Button
    private lateinit var productoViewModel: ProductoViewModel
    private lateinit var productoAdapter: ProductoAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consulta)
        
        initViews()
        setupViewModel()
        setupAdapter()
        setupClickListeners()
        observeViewModel()
    }
    
    private fun initViews() {
        editTextBusqueda = findViewById(R.id.editTextBusqueda)
        buttonBuscar = findViewById(R.id.buttonBuscar)
        listViewResultados = findViewById(R.id.listViewResultados)
        buttonVolver = findViewById(R.id.buttonVolver)
    }
    
    private fun setupViewModel() {
        productoViewModel = ViewModelProvider(this, AndroidViewModelFactory.getInstance(application))[ProductoViewModel::class.java]
    }
    
    private fun setupAdapter() {
        productoAdapter = ProductoAdapter(this, emptyList())
        listViewResultados.adapter = productoAdapter
    }
    
    private fun setupClickListeners() {
        buttonBuscar.setOnClickListener {
            val termino = editTextBusqueda.text.toString().trim()
            if (termino.isNotEmpty()) {
                productoViewModel.buscarProductos(termino)
            } else {
                Toast.makeText(this, "Ingresa un término de búsqueda", Toast.LENGTH_SHORT).show()
            }
        }
        
        buttonVolver.setOnClickListener {
            finish()
        }
        
        listViewResultados.setOnItemClickListener { _, _, position, _ ->
            val producto = productoAdapter.getItem(position)
            mostrarDetallesYCompartir(producto)
        }
    }
    
    private fun observeViewModel() {
        productoViewModel.productosFiltrados.observe(this) { productos ->
            productoAdapter.actualizarProductos(productos)
        }
        
        productoViewModel.mensaje.observe(this) { mensaje ->
            if (mensaje.isNotEmpty()) {
                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
                productoViewModel.limpiarMensaje()
            }
        }
    }
    
    private fun mostrarDetallesYCompartir(producto: Producto) {
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
