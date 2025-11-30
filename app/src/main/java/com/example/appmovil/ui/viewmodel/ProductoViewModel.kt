package com.example.appmovil.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.appmovil.data.AppDatabase
import com.example.appmovil.data.MovimientoInventario
import com.example.appmovil.data.Producto
import kotlinx.coroutines.launch
import java.util.Date

class ProductoViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = AppDatabase.getDatabase(application)
    private val productoDao = database.productoDao()
    private val movimientoDao = database.movimientoDao()
    
    val allProductos: LiveData<List<Producto>> = productoDao.getAllProductos()
    
    fun getAllMovimientos(): LiveData<List<MovimientoInventario>> = movimientoDao.getAllMovimientos()
    
    private val _productosFiltrados = MutableLiveData<List<Producto>>()
    val productosFiltrados: LiveData<List<Producto>> = _productosFiltrados
    
    private val _productoSeleccionado = MutableLiveData<Producto?>()
    val productoSeleccionado: LiveData<Producto?> = _productoSeleccionado
    
    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> = _mensaje
    
    private val _imagenCapturada = MutableLiveData<Pair<Bitmap?, String?>>()
    val imagenCapturada: LiveData<Pair<Bitmap?, String?>> = _imagenCapturada
    
    fun insertarProducto(producto: Producto) {
        viewModelScope.launch {
            try {
                productoDao.insertarProducto(producto)
                
                // Crear movimiento de ENTRADA
                val movimiento = MovimientoInventario(
                    productoId = producto.id,
                    nombreProducto = producto.nombre,
                    tipo = "ENTRADA",
                    cantidad = producto.cantidad,
                    cantidadAnterior = 0,
                    cantidadNueva = producto.cantidad,
                    fecha = Date().time
                )
                movimientoDao.insertarMovimiento(movimiento)
                
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
    
    suspend fun getProductoByIdSync(id: String): Producto? {
        return try {
            productoDao.getProductoById(id)
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun existeProductoConId(id: String): Boolean {
        return try {
            productoDao.getProductoById(id) != null
        } catch (e: Exception) {
            false
        }
    }
    
    fun limpiarMensaje() {
        _mensaje.value = ""
    }
    
    fun setImagenCapturada(bitmap: Bitmap, ruta: String) {
        _imagenCapturada.value = Pair(bitmap, ruta)
    }
    
    fun registrarSalida(productoId: String, cantidad: Int, tipoSalida: String = "SALIDA") {
        viewModelScope.launch {
            try {
                val producto = productoDao.getProductoById(productoId)
                if (producto == null) {
                    _mensaje.value = "Producto no encontrado"
                    return@launch
                }
                
                val nuevaCantidad = producto.cantidad - cantidad
                if (nuevaCantidad < 0) {
                    _mensaje.value = "No hay suficiente stock. Stock actual: ${producto.cantidad}"
                    return@launch
                }
                
                val cantidadAnterior = producto.cantidad
                val productoActualizado = producto.copy(cantidad = nuevaCantidad)
                productoDao.actualizarProducto(productoActualizado)
                
                // Crear movimiento de SALIDA o VENTA
                val tipoMovimiento = if (tipoSalida == "Venta") "VENTA" else "SALIDA"
                val movimiento = MovimientoInventario(
                    productoId = producto.id,
                    nombreProducto = producto.nombre,
                    tipo = tipoMovimiento,
                    cantidad = cantidad,
                    cantidadAnterior = cantidadAnterior,
                    cantidadNueva = nuevaCantidad,
                    fecha = Date().time
                )
                movimientoDao.insertarMovimiento(movimiento)
                
                _mensaje.value = "Salida registrada exitosamente. Nuevo stock: $nuevaCantidad"
            } catch (e: Exception) {
                _mensaje.value = "Error al registrar salida: ${e.message}"
            }
        }
    }
    
    suspend fun getUltimosMovimientos(limite: Int): List<MovimientoInventario> {
        return try {
            movimientoDao.getUltimosMovimientos(limite)
        } catch (e: Exception) {
            emptyList()
        }
    }
}

