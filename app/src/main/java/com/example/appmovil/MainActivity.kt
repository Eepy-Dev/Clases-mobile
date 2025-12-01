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
        
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return ProductViewModel(repository, userPreferencesRepository) as T
                }
                if (modelClass.isAssignableFrom(com.example.appmovil.ui.viewmodel.LoginViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return com.example.appmovil.ui.viewmodel.LoginViewModel(repository, userPreferencesRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
        
        val productViewModel = ViewModelProvider(this, factory)[ProductViewModel::class.java]
        val loginViewModel = ViewModelProvider(this, factory)[com.example.appmovil.ui.viewmodel.LoginViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            AppMovilTheme {
                AppNavigation(productViewModel = productViewModel, loginViewModel = loginViewModel)
            }
        }
    }
}