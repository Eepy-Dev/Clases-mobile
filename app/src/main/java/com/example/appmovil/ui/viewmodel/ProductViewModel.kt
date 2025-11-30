package com.example.appmovil.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovil.data.repository.ProductRepository
import com.example.appmovil.domain.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProductUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedProduct: Product? = null,
    val externalImageUrl: String? = null
)

class ProductViewModel(
    private val repository: ProductRepository = ProductRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
        loadExternalImage()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.getProducts()
            result.onSuccess { products ->
                _uiState.value = _uiState.value.copy(products = products, isLoading = false)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun loadExternalImage() {
        viewModelScope.launch {
            val result = repository.getRandomDogImage()
            result.onSuccess { url ->
                _uiState.value = _uiState.value.copy(externalImageUrl = url)
            }
        }
    }

    fun addProduct(nombre: String, precio: Double, stock: Int, imagenUrl: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val product = Product(nombre = nombre, precio = precio, stock = stock, imagenUrl = imagenUrl)
            val result = repository.createProduct(product)
            result.onSuccess {
                loadProducts() // Reload list
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun updateProduct(id: Long, nombre: String, precio: Double, stock: Int, imagenUrl: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val product = Product(id = id, nombre = nombre, precio = precio, stock = stock, imagenUrl = imagenUrl)
            val result = repository.updateProduct(id, product)
            result.onSuccess {
                loadProducts()
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun deleteProduct(id: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.deleteProduct(id)
            result.onSuccess {
                loadProducts()
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun searchProducts(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.searchProducts(query)
            result.onSuccess { products ->
                _uiState.value = _uiState.value.copy(products = products, isLoading = false)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun registerOutput(id: Long, cantidad: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.registerOutput(id, cantidad)
            result.onSuccess {
                loadProducts()
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun selectProduct(product: Product?) {
        _uiState.value = _uiState.value.copy(selectedProduct = product)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
