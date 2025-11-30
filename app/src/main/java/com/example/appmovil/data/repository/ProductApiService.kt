package com.example.appmovil.data.repository

import com.example.appmovil.domain.model.Product
import retrofit2.Response
import retrofit2.http.*

interface ProductApiService {

    @GET("products")
    suspend fun getProducts(): List<Product>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Long): Response<Product>

    @POST("products")
    suspend fun createProduct(@Body product: Product): Product

    @PUT("products/{id}")
    suspend fun updateProduct(@Path("id") id: Long, @Body product: Product): Product

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: Long): Response<Unit>

    @GET("products/search")
    suspend fun searchProducts(@Query("nombre") nombre: String): List<Product>

    @POST("products/output")
    suspend fun registerOutput(@Body payload: Map<String, Any>): Product
}
