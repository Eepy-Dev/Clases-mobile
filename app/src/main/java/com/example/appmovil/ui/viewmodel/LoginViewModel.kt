package com.example.appmovil.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    
    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> = _loginResult
    
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    
    fun validarLogin(usuario: String, contrasena: String) {
        when {
            usuario.isEmpty() -> {
                _errorMessage.value = "El campo usuario no puede estar vacío"
                _loginResult.value = false
            }
            contrasena.isEmpty() -> {
                _errorMessage.value = "El campo contraseña no puede estar vacío"
                _loginResult.value = false
            }
            usuario == "admin" && contrasena == "admin" -> {
                _loginResult.value = true
            }
            else -> {
                _errorMessage.value = "Usuario o contraseña incorrectos"
                _loginResult.value = false
            }
        }
    }
}

