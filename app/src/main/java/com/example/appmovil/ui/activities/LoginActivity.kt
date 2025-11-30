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
import com.example.appmovil.ui.screens.LoginScreen
import com.example.appmovil.ui.theme.AppMovilTheme
import com.example.appmovil.ui.viewmodel.LoginViewModel

class LoginActivity : ComponentActivity() {
    
    private lateinit var loginViewModel: LoginViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setupViewModel()
        
        setContent {
            AppMovilTheme {
                LoginScreen(
                    onLoginSuccess = {
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                        finish()
                    },
                    onError = { mensaje ->
                        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
                    },
                    loginViewModel = loginViewModel
                )
            }
        }
    }
    
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
    
    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(this, AndroidViewModelFactory.getInstance(application))[LoginViewModel::class.java]
    }
}

