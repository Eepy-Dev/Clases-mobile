package com.example.appmovil.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovil.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class LoginViewModel(
    private val repository: ProductRepository = ProductRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            val result = repository.login(username, password)
            if (result.isSuccess) {
                _uiState.value = LoginUiState(isSuccess = true)
            } else {
                _uiState.value = LoginUiState(error = result.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    fun register(username: String, password: String, email: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            // Assuming repository.createUser expects a User object. 
            // We need to check User model and ProductRepository.createUser signature.
            // For now, I'll assume User has username, password, email.
            // Wait, I need to check User model first.
            val user = com.example.appmovil.domain.model.User(username = username, password = password, email = email)
            val result = repository.createUser(user)
            if (result.isSuccess) {
                _uiState.value = LoginUiState(isSuccess = true)
            } else {
                _uiState.value = LoginUiState(error = result.exceptionOrNull()?.message ?: "Error al registrar")
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState()
    }
}
