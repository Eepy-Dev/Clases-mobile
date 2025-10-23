package com.example.appmovil

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class IngresoActivity : AppCompatActivity() {
    
    private lateinit var editTextId: EditText
    private lateinit var editTextNombre: EditText
    private lateinit var editTextDescripcion: EditText
    private lateinit var editTextPrecio: EditText
    private lateinit var editTextCantidad: EditText
    private lateinit var imageViewFoto: ImageView
    private lateinit var buttonTomarFoto: Button
    private lateinit var buttonGuardar: Button
    private lateinit var buttonVolver: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingreso)
        
        initViews()
        setupClickListeners()
    }
    
    private fun initViews() {
        editTextId = findViewById(R.id.editTextId)
        editTextNombre = findViewById(R.id.editTextNombre)
        editTextDescripcion = findViewById(R.id.editTextDescripcion)
        editTextPrecio = findViewById(R.id.editTextPrecio)
        editTextCantidad = findViewById(R.id.editTextCantidad)
        imageViewFoto = findViewById(R.id.imageViewFoto)
        buttonTomarFoto = findViewById(R.id.buttonTomarFoto)
        buttonGuardar = findViewById(R.id.buttonGuardar)
        buttonVolver = findViewById(R.id.buttonVolver)
    }
    
    private fun setupClickListeners() {
        buttonTomarFoto.setOnClickListener {
            // TODO: Implementar lógica de cámara
        }
        
        buttonGuardar.setOnClickListener {
            // TODO: Implementar lógica de guardar producto
        }
        
        buttonVolver.setOnClickListener {
            finish()
        }
    }
}
