package com.example.appmovil.data.repository

import com.example.appmovil.data.remote.ProductApiService
import com.example.appmovil.data.remote.ExternalApiService
import com.example.appmovil.data.remote.RetrofitClient
import com.example.appmovil.domain.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class ProductRepository(
    private val api: ProductApiService = RetrofitClient.productApiService,
    private val externalApi: ExternalApiService = RetrofitClient.externalApiService
) {

    private fun <T> handle(response: Response<T>): Result<T> {
        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) Result.success(body)
            else Result.failure(Exception("Respuesta vacía del servidor"))
        } else {
            Result.failure(Exception("HTTP ${response.code()} - ${response.message()}"))
        }
    }

    suspend fun getProducts(): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            handle(api.getProducts())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductById(id: Long): Result<Product> = withContext(Dispatchers.IO) {
        try {
            handle(api.getProductById(id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createProduct(product: Product): Result<Product> = withContext(Dispatchers.IO) {
        try {
            handle(api.createProduct(product))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProduct(id: Long, product: Product): Result<Product> = withContext(Dispatchers.IO) {
        try {
            handle(api.updateProduct(id, product))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.deleteProduct(id)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Error al eliminar producto"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchProducts(nombre: String): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            handle(api.searchProducts(nombre))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerOutput(id: Long, cantidad: Int): Result<Product> = withContext(Dispatchers.IO) {
        try {
            val payload = mapOf("id" to id, "cantidad" to cantidad)
            handle(api.registerOutput(payload))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRandomDogImage(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val resp = externalApi.getRandomDogImage()
            Result.success(resp.message)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
