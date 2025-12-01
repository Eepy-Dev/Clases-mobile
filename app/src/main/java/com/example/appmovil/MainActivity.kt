package com.example.appmovil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.appmovil.data.local.AppDatabase
import com.example.appmovil.data.local.UserPreferencesRepository
import com.example.appmovil.data.repository.ProductRepository
import com.example.appmovil.ui.navigation.AppNavigation
import com.example.appmovil.ui.theme.AppMovilTheme
import com.example.appmovil.ui.viewmodel.LoginViewModel
import com.example.appmovil.ui.viewmodel.ProductViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "app-database"
        ).fallbackToDestructiveMigration().build()
        
        val repository = ProductRepository(
            productDao = db.productDao(),
            deletedProductDao = db.deletedProductDao()
        )
        val userPreferencesRepository = UserPreferencesRepository(applicationContext)
        
        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
                    return ProductViewModel(repository, userPreferencesRepository) as T
                }
                if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                    return LoginViewModel(repository, userPreferencesRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
        
        val productViewModel = ViewModelProvider(this, factory)[ProductViewModel::class.java]
        val loginViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            AppMovilTheme {
                AppNavigation(productViewModel = productViewModel, loginViewModel = loginViewModel)
            }
        }
    }
}