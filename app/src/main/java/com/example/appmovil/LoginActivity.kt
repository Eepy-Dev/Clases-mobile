package com.example.appmovil

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory

class LoginActivity : AppCompatActivity() {
    
    private lateinit var editTextUsuario: EditText
    private lateinit var editTextContrasena: EditText
    private lateinit var buttonLogin: Button
    private lateinit var loginViewModel: LoginViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        initViews()
        setupViewModel()
        setupClickListeners()
        observeViewModel()
    }
    
    private fun initViews() {
        editTextUsuario = findViewById(R.id.editTextUsuario)
        editTextContrasena = findViewById(R.id.editTextContrasena)
        buttonLogin = findViewById(R.id.buttonLogin)
    }
    
    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(this, AndroidViewModelFactory.getInstance(application))[LoginViewModel::class.java]
    }
    
    private fun setupClickListeners() {
        buttonLogin.setOnClickListener {
            val usuario = editTextUsuario.text.toString().trim()
            val contrasena = editTextContrasena.text.toString().trim()
            
            loginViewModel.validarLogin(usuario, contrasena)
        }
    }
    
    private fun observeViewModel() {
        loginViewModel.loginResult.observe(this) { esExitoso ->
            if (esExitoso) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        
        loginViewModel.errorMessage.observe(this) { mensaje ->
            if (mensaje.isNotEmpty()) {
                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
            }
        }
    }
}
