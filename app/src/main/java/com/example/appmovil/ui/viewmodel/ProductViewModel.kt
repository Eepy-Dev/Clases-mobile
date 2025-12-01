package com.example.appmovil.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovil.data.repository.ProductRepository
import com.example.appmovil.domain.model.Product
import com.example.appmovil.domain.model.InventoryMovement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

data class ProductUiState(
    val products: List<Product> = emptyList(),
    val inventoryMovements: List<InventoryMovement> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedProduct: Product? = null,
    val externalImageUrl: String? = null
)

class ProductViewModel(
    private val repository: ProductRepository = ProductRepository(),
    private val userPreferencesRepository: com.example.appmovil.data.local.UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()
    
    val userRole: StateFlow<String?> = userPreferencesRepository.userRole
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        loadProducts()

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

    fun loadInventoryMovements() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.getInventoryMovements()
            result.onSuccess { movements ->
                _uiState.value = _uiState.value.copy(inventoryMovements = movements, isLoading = false)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
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

    fun loadOnlineCatalog(onResult: (List<Product>) -> Unit) {
        viewModelScope.launch {
            val result = repository.getOnlineCatalog()
            result.onSuccess { products ->
                onResult(products)
            }.onFailure {
                onResult(emptyList())
            }
        }
    }

    fun loadDeletedProducts(onResult: (List<com.example.appmovil.data.local.entity.DeletedProductEntity>) -> Unit) {
        viewModelScope.launch {
            val result = repository.getDeletedProducts()
            result.onSuccess { products ->
                onResult(products)
            }.onFailure {
                onResult(emptyList())
            }
        }
    }

    fun getProductById(id: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.getProductById(id)
            result.onSuccess { product ->
                _uiState.value = _uiState.value.copy(selectedProduct = product, isLoading = false)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }
}

