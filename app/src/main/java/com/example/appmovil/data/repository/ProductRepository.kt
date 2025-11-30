package com.example.appmovil.data.repository

import com.example.appmovil.data.remote.RetrofitClient
import com.example.appmovil.domain.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductRepository(
    private val api: ProductApiService = RetrofitClient.productApiService,
    private val externalApi: ExternalApiService = RetrofitClient.externalApiService
) {

    suspend fun getProducts(): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val products = api.getProducts()
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductById(id: Long): Result<Product?> = withContext(Dispatchers.IO) {
        try {
            val response = api.getProductById(id)
            if (response.isSuccessful) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("Error fetching product: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createProduct(product: Product): Result<Product> = withContext(Dispatchers.IO) {
        try {
            val newProduct = api.createProduct(product)
            Result.success(newProduct)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProduct(id: Long, product: Product): Result<Product> = withContext(Dispatchers.IO) {
        try {
            val updatedProduct = api.updateProduct(id, product)
            Result.success(updatedProduct)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.deleteProduct(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error deleting product"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchProducts(nombre: String): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val products = api.searchProducts(nombre)
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerOutput(id: Long, cantidad: Int): Result<Product> = withContext(Dispatchers.IO) {
        try {
            val payload = mapOf("id" to id, "cantidad" to cantidad)
            val product = api.registerOutput(payload)
            Result.success(product)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRandomDogImage(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = externalApi.getRandomDogImage()
            Result.success(response.message)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
