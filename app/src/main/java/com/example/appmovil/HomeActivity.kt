package com.example.appmovil

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {
    
    private lateinit var buttonProductos: Button
    private lateinit var buttonConsulta: Button
    private lateinit var buttonIngreso: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        
        initViews()
        setupClickListeners()
    }
    
    private fun initViews() {
        buttonProductos = findViewById(R.id.buttonProductos)
        buttonConsulta = findViewById(R.id.buttonConsulta)
        buttonIngreso = findViewById(R.id.buttonIngreso)
    }
    
    private fun setupClickListeners() {
        buttonProductos.setOnClickListener {
            val intent = Intent(this, ProductosActivity::class.java)
            startActivity(intent)
        }
        
        buttonConsulta.setOnClickListener {
            val intent = Intent(this, ConsultaActivity::class.java)
            startActivity(intent)
        }
        
        buttonIngreso.setOnClickListener {
            val intent = Intent(this, IngresoActivity::class.java)
            startActivity(intent)
        }
    }
}
