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
    private val repository: ProductRepository = ProductRepository(),
    private val userPreferencesRepository: com.example.appmovil.data.local.UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)

            // Strict Email Validation
            val allowedEmails = listOf("admin@pasteleria.cl", "vendedor@pasteleria.cl")
            if (username !in allowedEmails) {
                _uiState.value = LoginUiState(error = "Acceso denegado. Solo personal autorizado.")
                return@launch
            }

            val result = repository.login(username, password)
            if (result.isSuccess) {
                // Mocking Role based on email for now since backend might not return it yet
                val role = if (username == "admin@pasteleria.cl") "ADMIN" else "VENDEDOR"
                val token = "mock_token_123" // In real app, get from result
                
                userPreferencesRepository.saveUserSession(token, role, username)
                _uiState.value = LoginUiState(isSuccess = true)
            } else {
                _uiState.value = LoginUiState(error = result.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    fun register(username: String, password: String, email: String) {
        viewModelScope.launch {
             _uiState.value = LoginUiState(error = "El registro público está deshabilitado.")
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState()
    }
}
