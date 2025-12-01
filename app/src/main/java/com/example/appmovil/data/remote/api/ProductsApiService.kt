package com.example.appmovil.data.remote.api

import com.example.appmovil.data.remote.model.ProductoBackend
import retrofit2.Response
import retrofit2.http.*

interface ProductsApiService {
    
    @GET("productos")
    suspend fun getAllProductos(): Response<List<ProductoBackend>>
    
    @GET("productos/{id}")
    suspend fun getProductoById(@Path("id") id: Long): Response<ProductoBackend>
    
    @POST("productos")
    suspend fun crearProducto(@Body producto: ProductoBackend): Response<ProductoBackend>
    
    @PUT("productos/{id}")
    suspend fun actualizarProducto(
        @Path("id") id: Long,
        @Body producto: ProductoBackend
    ): Response<ProductoBackend>
    
    @DELETE("productos/{id}")
    suspend fun eliminarProducto(@Path("id") id: Long): Response<Unit>
    
    @GET("productos/buscar")
    suspend fun buscarProductos(@Query("nombre") nombre: String): Response<List<ProductoBackend>>
}

