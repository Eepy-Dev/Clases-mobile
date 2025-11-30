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
import com.example.appmovil.data.ProductoExterno
import com.example.appmovil.network.RetrofitClient
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

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
    
    // LiveData para productos externos de la API
    private val _productosExternos = MutableLiveData<List<ProductoExterno>>()
    val productosExternos: LiveData<List<ProductoExterno>> = _productosExternos
    
    private val _cargandoProductosExternos = MutableLiveData<Boolean>()
    val cargandoProductosExternos: LiveData<Boolean> = _cargandoProductosExternos
    
    private val _errorProductosExternos = MutableLiveData<String?>()
    val errorProductosExternos: LiveData<String?> = _errorProductosExternos
    
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
    
    /**
     * Obtiene todos los productos de pastelería del backend simulado (MockAPI.io)
     */
    fun obtenerProductosExternos() {
        viewModelScope.launch {
            _cargandoProductosExternos.value = true
            _errorProductosExternos.value = null
            try {
                val productos = RetrofitClient.mockApiService.getAllProductos()
                _productosExternos.value = productos
            } catch (e: Exception) {
                _errorProductosExternos.value = "Error al obtener productos: ${e.message}. Asegúrate de configurar la URL de MockAPI.io"
                _productosExternos.value = emptyList()
            } finally {
                _cargandoProductosExternos.value = false
            }
        }
    }
    
    /**
     * Obtiene un producto específico del backend simulado por ID
     */
    suspend fun obtenerProductoExternoPorId(id: String): ProductoExterno? {
        return try {
            RetrofitClient.mockApiService.getProductoById(id)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Convierte un ProductoExterno a Producto local para agregarlo al inventario
     */
    fun convertirProductoExternoALocal(productoExterno: ProductoExterno): Producto {
        return Producto(
            id = "EXT-${productoExterno.getIdAsString()}",
            nombre = productoExterno.title,
            descripcion = productoExterno.description,
            precio = productoExterno.price,
            cantidad = 0, // Inicialmente sin stock
            foto = productoExterno.imagenUrl // URL de la imagen
        )
    }
    
    /**
     * Extrae el ID del servidor del ID local del producto
     * Si el ID local empieza con "EXT-", extrae el ID del servidor
     */
    private fun obtenerIdServidorDesdeIdLocal(idLocal: String): String? {
        return if (idLocal.startsWith("EXT-")) {
            idLocal.removePrefix("EXT-")
        } else {
            null
        }
    }
    
    /**
     * Busca un producto en el servidor por ID del servidor o por nombre
     */
    private suspend fun buscarProductoEnServidor(producto: Producto): ProductoExterno? {
        // Primero intentar buscar por ID del servidor si existe
        val idServidor = obtenerIdServidorDesdeIdLocal(producto.id)
        if (idServidor != null) {
            try {
                return RetrofitClient.mockApiService.getProductoById(idServidor)
            } catch (e: Exception) {
                // Si no se encuentra por ID, continuar con búsqueda por nombre
            }
        }
        
        // Si no se encuentra por ID, buscar por nombre y precio (más preciso)
        val productosServidor = RetrofitClient.mockApiService.getAllProductos()
        return productosServidor.find { 
            it.title == producto.nombre && 
            it.price == producto.precio 
        } ?: productosServidor.find { 
            it.title == producto.nombre 
        }
    }
    
    /**
     * Crea un nuevo producto en el servidor (POST)
     * Verifica si ya existe antes de crear para evitar duplicados
     */
    fun sincronizarProductoAlServidor(producto: Producto) {
        viewModelScope.launch {
            _cargandoProductosExternos.value = true
            _errorProductosExternos.value = null
            try {
                // Verificar si el producto ya existe en el servidor
                val productoExistente = buscarProductoEnServidor(producto)
                if (productoExistente != null) {
                    _errorProductosExternos.value = "El producto ya existe en el servidor. Use 'Actualizar en Servidor' para modificarlo."
                    return@launch
                }
                
                val productoExterno = ProductoExterno(
                    id = null, // MockAPI genera el ID automáticamente
                    title = producto.nombre,
                    price = producto.precio,
                    description = producto.descripcion,
                    category = null,
                    imagenUrl = producto.foto
                )
                val productoCreado = RetrofitClient.mockApiService.crearProducto(productoExterno)
                _mensaje.value = "Producto creado en el servidor exitosamente (ID: ${productoCreado.getIdAsString()})"
            } catch (e: Exception) {
                _errorProductosExternos.value = "Error al crear: ${e.message}"
            } finally {
                _cargandoProductosExternos.value = false
            }
        }
    }
    
    /**
     * Actualiza un producto en el servidor (PUT)
     * Busca el producto por ID del servidor (si existe) o por nombre/precio
     */
    fun actualizarProductoEnServidor(producto: Producto) {
        viewModelScope.launch {
            _cargandoProductosExternos.value = true
            _errorProductosExternos.value = null
            try {
                val productoEncontrado = buscarProductoEnServidor(producto)
                
                if (productoEncontrado == null) {
                    _errorProductosExternos.value = "Producto no encontrado en el servidor. Use 'Sincronizar' para crearlo primero."
                    return@launch
                }
                
                val productoIdServidor = productoEncontrado.getIdAsString()
                val productoExterno = ProductoExterno(
                    id = productoIdServidor,
                    title = producto.nombre,
                    price = producto.precio,
                    description = producto.descripcion,
                    category = null,
                    imagenUrl = producto.foto
                )
                RetrofitClient.mockApiService.actualizarProducto(productoIdServidor, productoExterno)
                _mensaje.value = "Producto actualizado en el servidor exitosamente"
            } catch (e: Exception) {
                _errorProductosExternos.value = "Error al actualizar: ${e.message}"
            } finally {
                _cargandoProductosExternos.value = false
            }
        }
    }
    
    /**
     * Elimina un producto del servidor (DELETE)
     * Busca el producto por ID del servidor (si existe) o por nombre/precio
     */
    fun eliminarProductoDelServidor(producto: Producto) {
        viewModelScope.launch {
            _cargandoProductosExternos.value = true
            _errorProductosExternos.value = null
            try {
                val productoEncontrado = buscarProductoEnServidor(producto)
                
                if (productoEncontrado == null) {
                    _errorProductosExternos.value = "Producto no encontrado en el servidor."
                    return@launch
                }
                
                val productoIdServidor = productoEncontrado.getIdAsString()
                RetrofitClient.mockApiService.eliminarProducto(productoIdServidor)
                _mensaje.value = "Producto eliminado del servidor exitosamente"
            } catch (e: Exception) {
                _errorProductosExternos.value = "Error al eliminar: ${e.message}"
            } finally {
                _cargandoProductosExternos.value = false
            }
        }
    }
    
    /**
     * Sincroniza masivamente todos los productos locales que no estén en el servidor
     * Solo crea productos que no existan (verifica por nombre y precio)
     */
    fun sincronizarTodosLosProductosAlServidor() {
        viewModelScope.launch {
            _cargandoProductosExternos.value = true
            _errorProductosExternos.value = null
            try {
                // Obtener todos los productos locales
                val productosLocales = productoDao.getAllProductosSuspend()
                
                if (productosLocales.isEmpty()) {
                    _mensaje.value = "No hay productos locales para sincronizar"
                    _cargandoProductosExternos.value = false
                    return@launch
                }
                
                // Obtener todos los productos del servidor una sola vez
                val productosServidor = RetrofitClient.mockApiService.getAllProductos()
                val nombresServidor = productosServidor.map { it.title to it.price }.toSet()
                
                var productosAgregados = 0
                var productosOmitidos = 0
                
                // Sincronizar cada producto local que no exista en el servidor
                for (productoLocal in productosLocales) {
                    try {
                        // Verificar si el producto ya existe en el servidor
                        val existeEnServidor = nombresServidor.contains(productoLocal.nombre to productoLocal.precio)
                        
                        if (!existeEnServidor) {
                            // El producto no existe, crearlo
                            val productoExterno = ProductoExterno(
                                id = null,
                                title = productoLocal.nombre,
                                price = productoLocal.precio,
                                description = productoLocal.descripcion,
                                category = null,
                                imagenUrl = productoLocal.foto
                            )
                            RetrofitClient.mockApiService.crearProducto(productoExterno)
                            productosAgregados++
                        } else {
                            productosOmitidos++
                        }
                    } catch (e: Exception) {
                        // Continuar con el siguiente producto si hay un error
                        productosOmitidos++
                    }
                }
                
                if (productosAgregados > 0) {
                    _mensaje.value = "Productos nuevos agregados al servidor: $productosAgregados"
                    if (productosOmitidos > 0) {
                        _mensaje.value += " ($productosOmitidos ya existían)"
                    }
                } else {
                    _mensaje.value = "Todos los productos locales ya existen en el servidor"
                }
            } catch (e: Exception) {
                _errorProductosExternos.value = "Error al sincronizar: ${e.message}"
            } finally {
                _cargandoProductosExternos.value = false
            }
        }
    }
}

