package com.example.appmovil.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class LoginValidationResult(
    val errores: Map<String, String> = emptyMap(),
    val esValido: Boolean = errores.isEmpty(),
    val credencialesCorrectas: Boolean = false
)

class LoginViewModel : ViewModel() {
    
    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> = _loginResult
    
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    
    fun validarLogin(usuario: String, contrasena: String): LoginValidationResult {
        val errores = mutableMapOf<String, String>()
        var credencialesCorrectas = false
        
        // Validar campos vacíos
        if (usuario.isEmpty()) {
            errores["usuario"] = "El campo usuario no puede estar vacío"
        }
        if (contrasena.isEmpty()) {
            errores["contrasena"] = "El campo contraseña no puede estar vacío"
        }
        
        // Si hay errores de campos vacíos, retornar sin validar credenciales
        if (errores.isNotEmpty()) {
            _loginResult.value = false
            return LoginValidationResult(errores, false, false)
        }
        
        // Validar credenciales
        if (usuario == "admin" && contrasena == "admin") {
            credencialesCorrectas = true
            _loginResult.value = true
            return LoginValidationResult(emptyMap(), true, true)
        } else {
            errores["general"] = "Usuario o contraseña incorrectos"
            _loginResult.value = false
            return LoginValidationResult(errores, false, false)
        }
    }
}

