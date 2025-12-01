package com.example.appmovil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.appmovil.data.local.AppDatabase
import com.example.appmovil.data.repository.ProductRepository
import com.example.appmovil.ui.navigation.AppNavigation
import com.example.appmovil.ui.theme.AppMovilTheme
import com.example.appmovil.ui.viewmodel.ProductViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "app-database"
        ).build()
        
        val repository = ProductRepository(productDao = db.productDao())
        
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return ProductViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
        
        val productViewModel = ViewModelProvider(this, factory)[ProductViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            AppMovilTheme {
                AppNavigation(productViewModel = productViewModel)
            }
        }
    }
}