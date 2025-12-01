package com.example.appmovil.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.appmovil.data.local.AppDatabase
import com.example.appmovil.data.local.entity.MovimientoInventario
import com.example.appmovil.data.local.entity.Producto
import com.example.appmovil.data.mapper.ProductMapper
import com.example.appmovil.data.repository.ProductRepository
import com.example.appmovil.data.remote.model.ProductoExterno
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductoViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = AppDatabase.getDatabase(application)
    private val repository = ProductRepository(
        database,
        database.productoDao(),
        database.movimientoDao()
    )
    
    val allProductos: LiveData<List<Producto>> = database.productoDao().getAllProductos()
    
    private val _productosFiltrados = MutableLiveData<List<Producto>>()
    val productosFiltrados: LiveData<List<Producto>> = _productosFiltrados
    
    private val _productoSeleccionado = MutableLiveData<Producto?>()
    val productoSeleccionado: LiveData<Producto?> = _productoSeleccionado
    
    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> = _mensaje
    
    private val _imagenCapturada = MutableLiveData<Pair<Bitmap?, String?>>()
    val imagenCapturada: LiveData<Pair<Bitmap?, String?>> = _imagenCapturada
    
    private val _productosExternos = MutableLiveData<List<ProductoExterno>>()
    val productosExternos: LiveData<List<ProductoExterno>> = _productosExternos
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private val _movimientos = MutableLiveData<List<MovimientoInventario>>()
    val movimientos: LiveData<List<MovimientoInventario>> = _movimientos
    
    fun insertarProducto(producto: Producto, sincronizar: Boolean = true) {
        viewModelScope.launch {
            try {
                repository.insertarProducto(producto, sincronizar)
                _mensaje.value = "Producto guardado exitosamente"
            } catch (e: Exception) {
                _mensaje.value = "Error al guardar el producto: ${e.message}"
            }
        }
    }
    
    fun actualizarProducto(producto: Producto) {
        viewModelScope.launch {
            try {
                repository.actualizarProducto(producto)
                _mensaje.value = "Producto actualizado exitosamente"
            } catch (e: Exception) {
                _mensaje.value = "Error al actualizar el producto: ${e.message}"
            }
        }
    }
    
    fun eliminarProducto(producto: Producto) {
        viewModelScope.launch {
            try {
                repository.eliminarProducto(producto)
                _mensaje.value = "Producto eliminado exitosamente"
            } catch (e: Exception) {
                _mensaje.value = "Error al eliminar el producto: ${e.message}"
            }
        }
    }
    
    fun buscarProductos(termino: String): List<Producto> {
        return try {
            viewModelScope.launch {
                val resultados = repository.buscarProductos(termino)
                _productosFiltrados.value = resultados
            }
            _productosFiltrados.value ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun buscarProductosSuspend(termino: String): List<Producto> {
        return repository.buscarProductos(termino)
    }
    
    fun cargarUltimosProductos(limit: Int = 10) {
        viewModelScope.launch {
            try {
                val productos = repository.getUltimosProductos(limit)
                _productosFiltrados.value = productos
            } catch (e: Exception) {
                Log.e("ProductoViewModel", "Error al cargar últimos productos", e)
                _productosFiltrados.value = emptyList()
            }
        }
    }
    
    fun getProductoById(id: String) {
        viewModelScope.launch {
            try {
                val producto = repository.getProductoById(id)
                _productoSeleccionado.value = producto
            } catch (e: Exception) {
                _mensaje.value = "Error al obtener el producto: ${e.message}"
            }
        }
    }
    
    suspend fun obtenerProductosExternos(): Result<List<ProductoExterno>> {
        _isLoading.value = true
        _error.value = null
        return repository.obtenerProductosExternos().also { result ->
            result.fold(
                onSuccess = {
                    _productosExternos.value = it
                    _isLoading.value = false
                },
                onFailure = {
                    _error.value = it.message
                    _isLoading.value = false
                }
            )
        }
    }
    
    suspend fun obtenerProductosDelServidor(): Result<List<Producto>> {
        _isLoading.value = true
        _error.value = null
        return repository.obtenerProductosDelServidor().also { result ->
            result.fold(
                onSuccess = {
                    _isLoading.value = false
                },
                onFailure = {
                    _error.value = it.message
                    _isLoading.value = false
                }
            )
        }
    }
    
    fun convertirProductoExternoALocal(productoExterno: ProductoExterno): Producto {
        return ProductMapper.toLocal(productoExterno)
    }
    
    fun sincronizarProductoAlServidor(producto: Producto) {
        viewModelScope.launch {
            try {
                val result = repository.sincronizarProductoAlBackend(producto)
                result.fold(
                    onSuccess = {
                        _mensaje.value = "Producto sincronizado exitosamente"
                    },
                    onFailure = {
                        _mensaje.value = "Error al sincronizar: ${it.message}"
                    }
                )
            } catch (e: Exception) {
                _mensaje.value = "Error: ${e.message}"
            }
        }
    }
    
    fun actualizarProductoEnServidor(producto: Producto) {
        viewModelScope.launch {
            try {
                val result = repository.actualizarProductoEnBackend(producto)
                result.fold(
                    onSuccess = {
                        _mensaje.value = "Producto actualizado en servidor"
                    },
                    onFailure = {
                        _mensaje.value = "Error al actualizar: ${it.message}"
                    }
                )
            } catch (e: Exception) {
                _mensaje.value = "Error: ${e.message}"
            }
        }
    }
    
    fun eliminarProductoDelServidor(producto: Producto) {
        viewModelScope.launch {
            try {
                val result = repository.eliminarProductoDelBackend(producto)
                result.fold(
                    onSuccess = {
                        _mensaje.value = "Producto eliminado del servidor"
                    },
                    onFailure = {
                        _mensaje.value = "Error al eliminar: ${it.message}"
                    }
                )
            } catch (e: Exception) {
                _mensaje.value = "Error: ${e.message}"
            }
        }
    }
    
    fun sincronizarTodosLosProductosAlServidor() {
        viewModelScope.launch {
            try {
                val result = repository.sincronizarTodosLosProductosAlBackend()
                result.fold(
                    onSuccess = { count ->
                        _mensaje.value = "Productos nuevos agregados al servidor: $count"
                    },
                    onFailure = {
                        _mensaje.value = "Error al sincronizar: ${it.message}"
                    }
                )
            } catch (e: Exception) {
                _mensaje.value = "Error: ${e.message}"
            }
        }
    }
    
    fun registrarSalida(producto: Producto, cantidad: Int, tipo: String) {
        viewModelScope.launch {
            try {
                val result = repository.registrarSalida(producto, cantidad, tipo)
                result.fold(
                    onSuccess = {
                        _mensaje.value = "Salida registrada exitosamente"
                    },
                    onFailure = {
                        _mensaje.value = "Error: ${it.message}"
                    }
                )
            } catch (e: Exception) {
                _mensaje.value = "Error: ${e.message}"
            }
        }
    }
    
    suspend fun obtenerMovimientos(): Result<List<MovimientoInventario>> {
        return try {
            val movimientos = repository.obtenerMovimientos()
            _movimientos.value = movimientos
            Result.success(movimientos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun limpiarMensaje() {
        _mensaje.value = ""
    }
    
    fun setImagenCapturada(bitmap: Bitmap, ruta: String) {
        _imagenCapturada.value = Pair(bitmap, ruta)
    }
    
    suspend fun generarSiguienteIdProd(): String {
        return repository.generarSiguienteIdProd()
    }
}
