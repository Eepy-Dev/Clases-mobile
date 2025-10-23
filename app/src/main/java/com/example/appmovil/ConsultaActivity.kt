package com.example.appmovil

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class ConsultaActivity : AppCompatActivity() {
    
    private lateinit var editTextBusqueda: EditText
    private lateinit var buttonBuscar: Button
    private lateinit var listViewResultados: ListView
    private lateinit var buttonVolver: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consulta)
        
        initViews()
        setupClickListeners()
    }
    
    private fun initViews() {
        editTextBusqueda = findViewById(R.id.editTextBusqueda)
        buttonBuscar = findViewById(R.id.buttonBuscar)
        listViewResultados = findViewById(R.id.listViewResultados)
        buttonVolver = findViewById(R.id.buttonVolver)
    }
    
    private fun setupClickListeners() {
        buttonBuscar.setOnClickListener {
            // TODO: Implementar lógica de búsqueda
        }
        
        buttonVolver.setOnClickListener {
            finish()
        }
        
        listViewResultados.setOnItemClickListener { _, _, position, _ ->
            // TODO: Implementar mostrar detalles y compartir
        }
    }
}
