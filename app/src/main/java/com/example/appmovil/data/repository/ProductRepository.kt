package com.example.appmovil.data.repository

import android.util.Log
import com.example.appmovil.data.local.AppDatabase
import com.example.appmovil.data.local.dao.MovimientoDao
import com.example.appmovil.data.local.dao.ProductoDao
import com.example.appmovil.data.local.entity.MovimientoInventario
import com.example.appmovil.data.local.entity.Producto
import com.example.appmovil.data.mapper.MovementMapper
import com.example.appmovil.data.mapper.ProductMapper
import com.example.appmovil.data.remote.RetrofitClient
import com.example.appmovil.data.remote.api.MockApiService
import com.example.appmovil.data.remote.model.ProductoExterno
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductRepository(
    private val database: AppDatabase,
    private val productoDao: ProductoDao,
    private val movimientoDao: MovimientoDao
) {
    
    private val productsApiService = RetrofitClient.productsApiService
    private val inventoryApiService = RetrofitClient.inventoryApiService
    private val mockApiService = RetrofitClient.mockApiService
    
    companion object {
        private const val TAG = "ProductRepository"
    }
    
    // Obtener todos los productos locales
    suspend fun getAllProductos(): List<Producto> = withContext(Dispatchers.IO) {
        productoDao.getAllProductosSuspend()
    }
    
    // Obtener producto por ID
    suspend fun getProductoById(id: String): Producto? = withContext(Dispatchers.IO) {
        productoDao.getProductoById(id)
    }
    
    // Buscar productos
    suspend fun buscarProductos(termino: String): List<Producto> = withContext(Dispatchers.IO) {
        productoDao.buscarProductos(termino)
    }
    
    // Obtener los últimos N productos
    suspend fun getUltimosProductos(limit: Int = 10): List<Producto> = withContext(Dispatchers.IO) {
        productoDao.getUltimosProductos(limit)
    }
    
    // Generar el siguiente ID PROD- corto (PROD-1, PROD-2, etc.)
    suspend fun generarSiguienteIdProd(): String = withContext(Dispatchers.IO) {
        try {
            val todosLosProductos = productoDao.getAllProductosSuspend()
            val idsProd = todosLosProductos
                .mapNotNull { producto ->
                    if (producto.id.startsWith("PROD-")) {
                        // Extraer el número después de "PROD-"
                        producto.id.substring(5).toLongOrNull()
                    } else {
                        null
                    }
                }
            
            val siguienteNumero = if (idsProd.isEmpty()) {
                1L
            } else {
                (idsProd.maxOrNull() ?: 0L) + 1L
            }
            
            "PROD-$siguienteNumero"
        } catch (e: Exception) {
            // Si hay error, usar timestamp como fallback
            "PROD-${System.currentTimeMillis()}"
        }
    }
    
    // Insertar producto localmente
    suspend fun insertarProducto(producto: Producto, sincronizar: Boolean = true) = withContext(Dispatchers.IO) {
        var productoConIdBackend = producto
        
        // Si no es EXT- y se solicita sincronización, sincronizar con backend (incluye PROD- y productos sin prefijo)
        // Los productos del catálogo web que ya existen en el backend no se sincronizan (sincronizar = false)
        if (!producto.id.startsWith("EXT-") && sincronizar) {
            try {
                Log.d(TAG, "🔄 Intentando sincronizar producto con backend: ${producto.nombre}, ID local: ${producto.id}")
                Log.d(TAG, "🌐 URL del backend: http://10.0.2.2:8081/productos")
                val productoBackend = ProductMapper.toBackend(producto)
                Log.d(TAG, "📤 Enviando producto al backend - ID: ${productoBackend.id}, Nombre: ${productoBackend.nombre}, Descripción: ${productoBackend.descripcion}, Precio: ${productoBackend.precio}, Stock: ${productoBackend.stock}")
                val response = productsApiService.crearProducto(productoBackend)
                Log.d(TAG, "📥 Respuesta del backend - Código: ${response.code()}, Éxito: ${response.isSuccessful}, Mensaje: ${response.message()}")
                if (response.body() != null) {
                    Log.d(TAG, "📦 Cuerpo de respuesta: ${response.body()}")
                }
                
                if (response.isSuccessful && response.body() != null) {
                    val productoBackendCreado = response.body()!!
                    Log.d(TAG, "✅ Producto sincronizado con backend: ${producto.nombre}, ID backend: ${productoBackendCreado.id}")
                    
                    // Actualizar el producto local con el ID del backend para futuras sincronizaciones
                    if (productoBackendCreado.id != null) {
                        productoConIdBackend = producto.copy(id = productoBackendCreado.id.toString())
                        // Intentar eliminar el producto anterior si tiene un ID diferente
                        if (producto.id != productoConIdBackend.id) {
                            try {
                                val productoAnterior = productoDao.getProductoById(producto.id)
                                if (productoAnterior != null) {
                                    productoDao.eliminarProducto(productoAnterior)
                                    Log.d(TAG, "🗑️ Producto anterior eliminado: ${producto.id}")
                                }
                            } catch (e: Exception) {
                                Log.w(TAG, "⚠️ No se pudo eliminar producto anterior: ${e.message}")
                            }
                        }
                        productoDao.insertarProducto(productoConIdBackend)
                        Log.d(TAG, "✅ Producto local actualizado con ID del backend: ${productoConIdBackend.id} (antes: ${producto.id})")
                        
                        // Registrar movimiento de entrada con el ID correcto (usar el ID actualizado del backend)
                        registrarMovimientoEntrada(productoConIdBackend, producto.cantidad)
                    } else {
                        // Si el backend no devolvió ID, mantener el ID original
                        Log.w(TAG, "⚠️ El backend no devolvió un ID para el producto: ${producto.nombre}, usando ID local: ${producto.id}")
                        productoDao.insertarProducto(producto)
                        // Registrar movimiento sin sincronizar si no hay ID del backend
                        registrarMovimientoEntrada(producto, producto.cantidad, sincronizar = false)
                    }
                } else {
                    // Si falla la sincronización, guardar localmente igualmente
                    productoDao.insertarProducto(producto)
                    Log.e(TAG, "❌ Error al sincronizar producto con backend: ${response.code()} - ${response.message()}")
                    response.errorBody()?.string()?.let { errorBody ->
                        Log.e(TAG, "Cuerpo del error: $errorBody")
                    }
                }
            } catch (e: java.net.ConnectException) {
                // Error de conexión - el backend no está disponible
                productoDao.insertarProducto(producto)
                Log.e(TAG, "❌ ERROR DE CONEXIÓN: No se puede conectar al backend en http://10.0.2.2:8081. Verifica que el servicio esté corriendo.", e)
                e.printStackTrace()
            } catch (e: java.net.SocketTimeoutException) {
                // Timeout - el backend tarda mucho en responder
                productoDao.insertarProducto(producto)
                Log.e(TAG, "❌ TIMEOUT: El backend no respondió a tiempo. Verifica que el servicio esté corriendo.", e)
                e.printStackTrace()
            } catch (e: Exception) {
                // Si falla la sincronización, guardar localmente igualmente
                productoDao.insertarProducto(producto)
                Log.e(TAG, "❌ ERROR GENERAL al sincronizar producto con backend", e)
                Log.e(TAG, "Tipo de error: ${e.javaClass.simpleName}, Mensaje: ${e.message}")
                e.printStackTrace()
            }
        } else {
            // Para productos EXT-, solo guardar localmente
            productoDao.insertarProducto(producto)
            // Registrar movimiento de entrada local (no sincronizar con backend)
            registrarMovimientoEntrada(producto, producto.cantidad, sincronizar = false)
        }
    }
    
    // Actualizar producto
    suspend fun actualizarProducto(producto: Producto) = withContext(Dispatchers.IO) {
        Log.d(TAG, "🔄 Actualizando producto local: ${producto.nombre}, ID: ${producto.id}")
        
        // Actualizar primero en la base de datos local
        productoDao.actualizarProducto(producto)
        
        // Si no es EXT-, sincronizar con backend
        if (!producto.id.startsWith("EXT-")) {
            try {
                val productoBackend = ProductMapper.toBackend(producto)
                
                // Si el producto tiene prefijo PROD-, significa que aún no se ha sincronizado
                // Hacer POST para crear nuevo producto en el backend
                if (producto.id.startsWith("PROD-")) {
                    Log.d(TAG, "⚠️ Producto PROD- sin sincronizar, creando en backend: ${producto.nombre}")
                    val createResponse = productsApiService.crearProducto(productoBackend)
                    
                    if (createResponse.isSuccessful && createResponse.body() != null) {
                        val productoBackendCreado = createResponse.body()!!
                        if (productoBackendCreado.id != null) {
                            // Actualizar el producto local con el ID del backend
                            val productoActualizado = producto.copy(id = productoBackendCreado.id.toString())
                            productoDao.eliminarProducto(producto)
                            productoDao.insertarProducto(productoActualizado)
                            Log.d(TAG, "✅ Producto PROD- sincronizado y actualizado con ID del backend: ${productoActualizado.id}")
                        }
                    } else {
                        Log.e(TAG, "❌ Error al sincronizar producto PROD- como nuevo: ${createResponse.code()} - ${createResponse.message()}")
                    }
                } else {
                    // El producto tiene ID numérico directo (sin prefijo PROD-), hacer PUT para actualizar
                    val idNumeric = producto.id.toLongOrNull()
                    if (idNumeric != null) {
                        Log.d(TAG, "📤 Actualizando producto en backend (PUT): ID=${idNumeric}, Nombre=${producto.nombre}")
                        val response = productsApiService.actualizarProducto(idNumeric, productoBackend)
                        
                        if (response.isSuccessful) {
                            Log.d(TAG, "✅ Producto actualizado en backend: ${producto.nombre}")
                        } else {
                            Log.e(TAG, "❌ Error al actualizar producto en backend: ${response.code()} - ${response.message()}")
                            response.errorBody()?.string()?.let { errorBody ->
                                Log.e(TAG, "Cuerpo del error: $errorBody")
                            }
                        }
                    } else {
                        Log.w(TAG, "⚠️ ID inválido para actualizar: ${producto.id}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Excepción al actualizar producto en backend", e)
                e.printStackTrace()
            }
        } else {
            Log.d(TAG, "ℹ️ Producto EXT- no se sincroniza con backend: ${producto.nombre}")
        }
    }
    
    // Eliminar producto
    suspend fun eliminarProducto(producto: Producto) = withContext(Dispatchers.IO) {
        Log.d(TAG, "🗑️ Eliminando producto: ${producto.nombre}, ID: ${producto.id}")
        
        // Eliminar primero de la base de datos local
        productoDao.eliminarProducto(producto)
        Log.d(TAG, "✅ Producto eliminado localmente: ${producto.nombre}")
        
        // Si no es EXT-, intentar eliminar del backend
        if (!producto.id.startsWith("EXT-")) {
            try {
                val idNumeric = extractNumericId(producto.id)
                
                if (idNumeric == null) {
                    // Si el producto tiene PROD- pero no ID numérico, significa que nunca se sincronizó
                    // Solo se elimina localmente
                    Log.d(TAG, "ℹ️ Producto PROD- sin ID numérico, solo eliminado localmente: ${producto.nombre}")
                } else {
                    // El producto tiene ID numérico, eliminar del backend
                    Log.d(TAG, "📤 Eliminando producto del backend (DELETE): ID=${idNumeric}, Nombre=${producto.nombre}")
                    val response = productsApiService.eliminarProducto(idNumeric)
                    
                    if (response.isSuccessful) {
                        Log.d(TAG, "✅ Producto eliminado del backend: ${producto.nombre}")
                    } else {
                        Log.e(TAG, "❌ Error al eliminar producto del backend: ${response.code()} - ${response.message()}")
                        response.errorBody()?.string()?.let { errorBody ->
                            Log.e(TAG, "Cuerpo del error: $errorBody")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Excepción al eliminar producto del backend", e)
                e.printStackTrace()
            }
        } else {
            Log.d(TAG, "ℹ️ Producto EXT- no se elimina del backend, solo localmente: ${producto.nombre}")
        }
    }
    
    // Obtener productos del catálogo externo (MockAPI)
    suspend fun obtenerProductosExternos(): Result<List<ProductoExterno>> = withContext(Dispatchers.IO) {
        try {
            val response = mockApiService.getProductosExternos()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener productos externos: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener productos externos", e)
            Result.failure(e)
        }
    }
    
    // Obtener productos del servidor Spring Boot
    suspend fun obtenerProductosDelServidor(): Result<List<Producto>> = withContext(Dispatchers.IO) {
        try {
            val response = productsApiService.getAllProductos()
            if (response.isSuccessful && response.body() != null) {
                val productos = ProductMapper.fromBackendList(response.body()!!)
                Result.success(productos)
            } else {
                Result.failure(Exception("Error al obtener productos del servidor: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener productos del servidor", e)
            Result.failure(e)
        }
    }
    
    // Sincronizar producto individual con el backend (POST)
    suspend fun sincronizarProductoAlBackend(producto: Producto): Result<Unit> = withContext(Dispatchers.IO) {
        if (producto.id.startsWith("EXT-")) {
            return@withContext Result.failure(Exception("Los productos EXT- no se sincronizan"))
        }
        
        try {
            val productoBackend = ProductMapper.toBackend(producto)
            val response = productsApiService.crearProducto(productoBackend)
            
            if (response.isSuccessful) {
                Log.d(TAG, "Producto sincronizado: ${producto.nombre}")
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al sincronizar: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al sincronizar producto", e)
            Result.failure(e)
        }
    }
    
    // Actualizar producto en el backend (PUT)
    suspend fun actualizarProductoEnBackend(producto: Producto): Result<Unit> = withContext(Dispatchers.IO) {
        if (producto.id.startsWith("EXT-")) {
            return@withContext Result.failure(Exception("Los productos EXT- no se sincronizan"))
        }
        
        try {
            val id = extractNumericId(producto.id) ?: return@withContext Result.failure(Exception("ID inválido"))
            val productoBackend = ProductMapper.toBackend(producto)
            val response = productsApiService.actualizarProducto(id, productoBackend)
            
            if (response.isSuccessful) {
                Log.d(TAG, "Producto actualizado en backend: ${producto.nombre}")
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al actualizar: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar producto en backend", e)
            Result.failure(e)
        }
    }
    
    // Eliminar producto del backend (DELETE)
    suspend fun eliminarProductoDelBackend(producto: Producto): Result<Unit> = withContext(Dispatchers.IO) {
        if (producto.id.startsWith("EXT-")) {
            return@withContext Result.failure(Exception("Los productos EXT- no se sincronizan"))
        }
        
        try {
            val id = extractNumericId(producto.id) ?: return@withContext Result.failure(Exception("ID inválido"))
            val response = productsApiService.eliminarProducto(id)
            
            if (response.isSuccessful) {
                Log.d(TAG, "Producto eliminado del backend: ${producto.nombre}")
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al eliminar producto del backend", e)
            Result.failure(e)
        }
    }
    
    // Sincronizar todos los productos locales al backend
    suspend fun sincronizarTodosLosProductosAlBackend(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val productosLocales = getAllProductos()
            val productosNoExt = productosLocales.filter { !it.id.startsWith("EXT-") }
            
            // Obtener productos existentes en el backend
            val response = productsApiService.getAllProductos()
            if (!response.isSuccessful || response.body() == null) {
                return@withContext Result.failure(Exception("Error al obtener productos del backend"))
            }
            
            val productosBackend = response.body()!!
            val nombresExistentes = productosBackend.map { it.nombre.lowercase() }.toSet()
            
            var sincronizados = 0
            for (producto in productosNoExt) {
                // Solo sincronizar si no existe en el backend
                if (!nombresExistentes.contains(producto.nombre.lowercase())) {
                    val productoBackend = ProductMapper.toBackend(producto)
                    val createResponse = productsApiService.crearProducto(productoBackend)
                    if (createResponse.isSuccessful) {
                        sincronizados++
                    }
                }
            }
            
            Result.success(sincronizados)
        } catch (e: Exception) {
            Log.e(TAG, "Error al sincronizar productos masivamente", e)
            Result.failure(e)
        }
    }
    
    // Registrar salida de producto
    suspend fun registrarSalida(
        producto: Producto,
        cantidad: Int,
        tipo: String // SALIDA_VENTA, SALIDA_MERMA, SALIDA_MOVIMIENTO
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val stockAnterior = producto.cantidad
            val nuevoStock = stockAnterior - cantidad
            
            if (nuevoStock < 0) {
                return@withContext Result.failure(Exception("Stock insuficiente"))
            }
            
            // Obtener el producto actualizado de la BD para asegurar que tenemos el ID correcto (por si se actualizó)
            val productoEnBD = productoDao.getProductoById(producto.id) ?: producto
            
            // Actualizar stock local
            val productoActualizado = productoEnBD.copy(cantidad = nuevoStock)
            productoDao.actualizarProducto(productoActualizado)
            
            // Usar el ID del producto de la BD (puede tener ID numérico si ya se sincronizó)
            val productoIdParaMovimiento = productoEnBD.id
            
            // Registrar movimiento local
            val movimiento = MovimientoInventario(
                productoId = productoIdParaMovimiento,
                nombreProducto = producto.nombre,
                tipo = tipo,
                cantidad = cantidad,
                stockAnterior = stockAnterior,
                stockNuevo = nuevoStock
            )
            movimientoDao.insertarMovimiento(movimiento)
            
            // Sincronizar movimiento con backend solo si el producto no es EXT-
            if (!productoIdParaMovimiento.startsWith("EXT-")) {
                sincronizarMovimientoAlBackend(movimiento, productoIdParaMovimiento)
            }
            
            // Actualizar stock en backend
            if (!productoIdParaMovimiento.startsWith("EXT-")) {
                try {
                    val productoBackend = ProductMapper.toBackend(productoActualizado)
                    val idNumeric = extractNumericId(productoIdParaMovimiento)
                    
                    if (idNumeric != null) {
                        Log.d(TAG, "📤 Actualizando stock en backend: ID=${idNumeric}, Stock anterior=${stockAnterior}, Stock nuevo=${nuevoStock}")
                        val response = productsApiService.actualizarProducto(idNumeric, productoBackend)
                        
                        if (response.isSuccessful) {
                            Log.d(TAG, "✅ Stock actualizado en backend")
                        } else {
                            Log.e(TAG, "❌ Error al actualizar stock en backend: ${response.code()} - ${response.message()}")
                        }
                    } else {
                        Log.w(TAG, "⚠️ No se puede actualizar stock en backend: producto tiene ID PROD- sin sincronizar")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Excepción al actualizar stock en backend", e)
                    e.printStackTrace()
                }
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al registrar salida", e)
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    // Obtener movimientos
    suspend fun obtenerMovimientos(): List<MovimientoInventario> = withContext(Dispatchers.IO) {
        movimientoDao.getUltimosMovimientos()
    }
    
    // Registrar movimiento de entrada
    private suspend fun registrarMovimientoEntrada(producto: Producto, cantidad: Int, sincronizar: Boolean = true) {
        try {
            Log.d(TAG, "📝 Registrando movimiento de entrada: Producto=${producto.nombre}, ID=${producto.id}, Cantidad=$cantidad")
            
            val movimiento = MovimientoInventario(
                productoId = producto.id,
                nombreProducto = producto.nombre,
                tipo = "ENTRADA",
                cantidad = cantidad,
                stockAnterior = 0,
                stockNuevo = producto.cantidad
            )
            movimientoDao.insertarMovimiento(movimiento)
            Log.d(TAG, "✅ Movimiento de entrada registrado localmente")
            
            // Sincronizar con backend solo si se solicita y el producto no es EXT-
            if (sincronizar && !producto.id.startsWith("EXT-")) {
                Log.d(TAG, "🔄 Sincronizando movimiento de entrada con backend para producto: ${producto.id}")
                sincronizarMovimientoAlBackend(movimiento, producto.id)
            } else {
                if (producto.id.startsWith("EXT-")) {
                    Log.d(TAG, "ℹ️ Movimiento EXT- no se sincroniza con backend")
                } else {
                    Log.d(TAG, "ℹ️ Sincronización de movimiento deshabilitada para: ${producto.id}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al registrar movimiento de entrada", e)
            e.printStackTrace()
        }
    }
    
    // Sincronizar movimiento con backend
    private suspend fun sincronizarMovimientoAlBackend(movimiento: MovimientoInventario, productoId: String) {
        try {
            Log.d(TAG, "🔄 Intentando sincronizar movimiento: Tipo=${movimiento.tipo}, Producto=${movimiento.nombreProducto}, productoId=$productoId")
            
            var productoIdParaMovimiento = productoId
            
            // Si el productoId es PROD- y no tiene ID numérico, intentar obtener el producto actualizado de la BD
            if (productoId.startsWith("PROD-")) {
                try {
                    val producto = productoDao.getProductoById(productoId)
                    if (producto != null && !producto.id.startsWith("PROD-") && !producto.id.startsWith("EXT-")) {
                        // El producto ya tiene ID numérico del backend
                        productoIdParaMovimiento = producto.id
                        Log.d(TAG, "✅ Producto actualizado encontrado con ID numérico: $productoIdParaMovimiento (antes: $productoId)")
                    } else {
                        Log.w(TAG, "⚠️ Producto PROD- aún no tiene ID numérico del backend, movimiento no se sincronizará: $productoId")
                        return
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Error al buscar producto actualizado: ${e.message}")
                    return
                }
            }
            
            // Crear movimiento con el productoId correcto para el backend
            val movimientoConIdCorrecto = movimiento.copy(productoId = productoIdParaMovimiento)
            
            // Si el productoId cambió, actualizar el movimiento en la BD (opcional, el movimiento ya existe localmente)
            // No actualizamos para no cambiar el ID del movimiento, solo usamos el ID correcto para sincronizar
            
            // Convertir a dominio del backend
            val movimientoBackend = MovementMapper.toDomain(movimientoConIdCorrecto)
            
            // Si el mapper retorna null, significa que el productoId no es válido
            if (movimientoBackend == null) {
                Log.w(TAG, "⚠️ Movimiento no sincronizado: productoId inválido o es EXT-/PROD-: $productoIdParaMovimiento")
                return
            }
            
            // Asegurar que el productoId sea válido
            if (movimientoBackend.productoId <= 0) {
                Log.e(TAG, "❌ Movimiento no sincronizado: productoId inválido en movimientoBackend: ${movimientoBackend.productoId}")
                return
            }
            
            Log.d(TAG, "📤 Enviando movimiento al backend: productoId=${movimientoBackend.productoId}, tipo=${movimientoBackend.tipo}, cantidad=${movimientoBackend.cantidad}")
            val response = inventoryApiService.crearMovimiento(movimientoBackend)
            
            if (response.isSuccessful) {
                Log.d(TAG, "✅ Movimiento sincronizado con backend: ${movimiento.tipo} - ${movimiento.nombreProducto} (productoId: ${movimientoBackend.productoId})")
            } else {
                Log.e(TAG, "❌ Error al sincronizar movimiento: código=${response.code()}, mensaje=${response.message()}")
                try {
                    response.errorBody()?.string()?.let { errorBody ->
                        Log.e(TAG, "Cuerpo del error: $errorBody")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error al leer cuerpo del error: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Excepción al sincronizar movimiento con backend", e)
            e.printStackTrace()
        }
    }
    
    // Helper para extraer ID numérico
    private fun extractNumericId(id: String): Long? {
        return when {
            id.startsWith("EXT-") -> null // Los productos EXT- no se sincronizan
            id.startsWith("PROD-") -> {
                // Extraer el número después de "PROD-", ej: "PROD-1" -> 1
                id.substring(5).toLongOrNull()
            }
            else -> id.toLongOrNull() // IDs numéricos directos (del backend)
        }
    }
}

