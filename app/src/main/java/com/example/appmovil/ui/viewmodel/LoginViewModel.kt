package com.example.appmovil.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    
    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> = _loginResult
    
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    
    private val _errorUsuario = MutableLiveData<String>()
    val errorUsuario: LiveData<String> = _errorUsuario
    
    private val _errorContrasena = MutableLiveData<String>()
    val errorContrasena: LiveData<String> = _errorContrasena
    
    fun validarLogin(usuario: String, contrasena: String) {
        _errorUsuario.value = ""
        _errorContrasena.value = ""
        _errorMessage.value = ""
        
        when {
            usuario.isEmpty() -> {
                _errorUsuario.value = "El campo usuario no puede estar vacío"
                _loginResult.value = false
            }
            contrasena.isEmpty() -> {
                _errorContrasena.value = "El campo contraseña no puede estar vacío"
                _loginResult.value = false
            }
            usuario == "admin" && contrasena == "admin" -> {
                _loginResult.value = true
            }
            else -> {
                _errorContrasena.value = "Usuario o contraseña incorrectos"
                _loginResult.value = false
            }
        }
    }
}

