package com.example.appmovil.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.appmovil.ui.screens.HomeScreen
import com.example.appmovil.ui.theme.AppMovilTheme

class HomeActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            AppMovilTheme {
                HomeScreen(
                    onProductosClick = {
                        val intent = Intent(this, ProductosActivity::class.java)
                        startActivity(intent)
                    },
                    onConsultaClick = {
                        val intent = Intent(this, ConsultaActivity::class.java)
                        startActivity(intent)
                    },
                    onIngresoClick = {
                        val intent = Intent(this, IngresoActivity::class.java)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

