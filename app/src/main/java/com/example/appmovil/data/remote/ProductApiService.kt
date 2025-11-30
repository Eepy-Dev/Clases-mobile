package com.example.appmovil.data.remote

import com.example.appmovil.domain.model.Product
import retrofit2.Response
import retrofit2.http.*

interface ProductApiService {
    @GET("productos")
    suspend fun getProducts(): List<Product>

    @GET("productos/{id}")
    suspend fun getProductById(@Path("id") id: Long): Response<Product>

    @POST("productos")
    suspend fun createProduct(@Body product: Product): Product

    @PUT("productos/{id}")
    suspend fun updateProduct(@Path("id") id: Long, @Body product: Product): Product

    @DELETE("productos/{id}")
    suspend fun deleteProduct(@Path("id") id: Long): Response<Void>

    @GET("productos/buscar")
    suspend fun searchProducts(@Query("nombre") name: String): List<Product>

    @POST("productos/salida")
    suspend fun registerOutput(@Body payload: Map<String, Any>): Product
}
