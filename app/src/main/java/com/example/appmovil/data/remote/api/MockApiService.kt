package com.example.appmovil.data.remote.api

import com.example.appmovil.data.remote.model.ProductoExterno
import retrofit2.Response
import retrofit2.http.*

interface MockApiService {
    
    @GET("productos")
    suspend fun getProductosExternos(): Response<List<ProductoExterno>>
    
    @GET("productos/{id}")
    suspend fun getProductoExternoById(@Path("id") id: String): Response<ProductoExterno>
    
    @POST("productos")
    suspend fun crearProductoExterno(@Body producto: ProductoExterno): Response<ProductoExterno>
    
    @PUT("productos/{id}")
    suspend fun actualizarProductoExterno(
        @Path("id") id: String,
        @Body producto: ProductoExterno
    ): Response<ProductoExterno>
    
    @DELETE("productos/{id}")
    suspend fun eliminarProductoExterno(@Path("id") id: String): Response<Unit>
}

