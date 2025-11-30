package com.example.appmovil.network

import com.example.appmovil.data.ProductoExterno
import retrofit2.http.*

/**
 * Interfaz de servicio Retrofit para consumir MockAPI.io (Backend simulado)
 * 
 * Este servicio permite sincronizar productos de pastelería desde un backend simulado
 * 
 * Endpoints disponibles:
 * - GET /productos - Obtener todos los productos de pastelería
 * - GET /productos/{id} - Obtener un producto por ID
 * - POST /productos - Crear un nuevo producto (para el paso 2)
 * - PUT /productos/{id} - Actualizar un producto (para el paso 2)
 * - DELETE /productos/{id} - Eliminar un producto (para el paso 2)
 */
interface MockApiService {
    
    @GET("productos")
    suspend fun getAllProductos(): List<ProductoExterno>
    
    @GET("productos/{id}")
    suspend fun getProductoById(@Path("id") id: String): ProductoExterno
    
    // Endpoints para el paso 2 (operaciones CRUD)
    @POST("productos")
    suspend fun crearProducto(@Body producto: ProductoExterno): ProductoExterno
    
    @PUT("productos/{id}")
    suspend fun actualizarProducto(@Path("id") id: String, @Body producto: ProductoExterno): ProductoExterno
    
    @DELETE("productos/{id}")
    suspend fun eliminarProducto(@Path("id") id: String): ProductoExterno
}

