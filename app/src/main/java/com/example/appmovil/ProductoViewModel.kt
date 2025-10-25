package com.example.appmovil

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ProductoViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = AppDatabase.getDatabase(application)
    private val productoDao = database.productoDao()
    
    val allProductos: LiveData<List<Producto>> = productoDao.getAllProductos()
    
    private val _productosFiltrados = MutableLiveData<List<Producto>>()
    val productosFiltrados: LiveData<List<Producto>> = _productosFiltrados
    
    private val _productoSeleccionado = MutableLiveData<Producto?>()
    val productoSeleccionado: LiveData<Producto?> = _productoSeleccionado
    
    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> = _mensaje
    
    fun insertarProducto(producto: Producto) {
        viewModelScope.launch {
            try {
                productoDao.insertarProducto(producto)
                _mensaje.value = "Producto guardado exitosamente"
            } catch (e: Exception) {
                _mensaje.value = "Error al guardar el producto: ${e.message}"
            }
        }
    }
    
    fun actualizarProducto(producto: Producto) {
        viewModelScope.launch {
            try {
                productoDao.actualizarProducto(producto)
                _mensaje.value = "Producto actualizado exitosamente"
            } catch (e: Exception) {
                _mensaje.value = "Error al actualizar el producto: ${e.message}"
            }
        }
    }
    
    fun eliminarProducto(producto: Producto) {
        viewModelScope.launch {
            try {
                productoDao.eliminarProducto(producto)
                _mensaje.value = "Producto eliminado exitosamente"
            } catch (e: Exception) {
                _mensaje.value = "Error al eliminar el producto: ${e.message}"
            }
        }
    }
    
    fun buscarProductos(termino: String) {
        viewModelScope.launch {
            try {
                val resultados = productoDao.buscarProductos(termino)
                _productosFiltrados.value = resultados
            } catch (e: Exception) {
                _mensaje.value = "Error en la búsqueda: ${e.message}"
            }
        }
    }
    
    fun getProductoById(id: String) {
        viewModelScope.launch {
            try {
                val producto = productoDao.getProductoById(id)
                _productoSeleccionado.value = producto
            } catch (e: Exception) {
                _mensaje.value = "Error al obtener el producto: ${e.message}"
            }
        }
    }
    
    fun limpiarMensaje() {
        _mensaje.value = ""
    }
}
