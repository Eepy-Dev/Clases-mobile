package com.example.appmovil

import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class ProductosActivity : AppCompatActivity() {
    
    private lateinit var listViewProductos: ListView
    private lateinit var buttonVolver: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos)
        
        initViews()
        setupClickListeners()
    }
    
    private fun initViews() {
        listViewProductos = findViewById(R.id.listViewProductos)
        buttonVolver = findViewById(R.id.buttonVolver)
    }
    
    private fun setupClickListeners() {
        buttonVolver.setOnClickListener {
            finish()
        }
        
        // TODO: Implementar lógica de lista de productos
        listViewProductos.setOnItemClickListener { _, _, position, _ ->
            // TODO: Implementar eliminación de producto
        }
    }
}
