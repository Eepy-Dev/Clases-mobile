package com.example.appmovil

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    
    private lateinit var editTextUsuario: EditText
    private lateinit var editTextContrasena: EditText
    private lateinit var buttonLogin: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        initViews()
        setupClickListeners()
    }
    
    private fun initViews() {
        editTextUsuario = findViewById(R.id.editTextUsuario)
        editTextContrasena = findViewById(R.id.editTextContrasena)
        buttonLogin = findViewById(R.id.buttonLogin)
    }
    
    private fun setupClickListeners() {
        buttonLogin.setOnClickListener {
            // TODO: Implementar lógica de login
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}
