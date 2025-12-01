package com.example.appmovil.data.repository

import com.example.appmovil.data.remote.ProductApiService
import com.example.appmovil.data.remote.ExternalApiService
import com.example.appmovil.data.remote.InventoryApiService
import com.example.appmovil.data.remote.UserApiService
import com.example.appmovil.data.remote.RetrofitClient
import com.example.appmovil.domain.model.Product
import com.example.appmovil.domain.model.User
import com.example.appmovil.domain.model.InventoryMovement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class ProductRepository(
    private val api: ProductApiService = RetrofitClient.productApiService,
    private val inventoryApi: InventoryApiService = RetrofitClient.inventoryApiService,
    private val userApi: UserApiService = RetrofitClient.userApiService,
    private val externalApi: ExternalApiService = RetrofitClient.externalApiService,
    private val productDao: com.example.appmovil.data.local.dao.ProductDao? = null
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

    // Product Methods
    suspend fun getProducts(): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            // Try to fetch from API
            val response = api.getProducts()
            if (response.isSuccessful && response.body() != null) {
                val products = response.body()!!
                // Update local database
                productDao?.let { dao ->
                    dao.deleteAll()
                    dao.insertAll(products.map { com.example.appmovil.data.local.entity.ProductEntity.fromDomain(it) })
                }
                Result.success(products)
            } else {
                // Fallback to local database
                productDao?.let { dao ->
                    val localProducts = dao.getAllProducts().map { it.toDomain() }
                    if (localProducts.isNotEmpty()) {
                        Result.success(localProducts)
                    } else {
                        Result.failure(Exception("No data available locally or remotely"))
                    }
                } ?: Result.failure(Exception("Error fetching data and no local database"))
            }
        } catch (e: Exception) {
            // Fallback to local database on error
            productDao?.let { dao ->
                val localProducts = dao.getAllProducts().map { it.toDomain() }
                if (localProducts.isNotEmpty()) {
                    Result.success(localProducts)
                } else {
                    Result.failure(e)
                }
            } ?: Result.failure(e)
        }
    }

    suspend fun getProductById(id: Long): Result<Product> = withContext(Dispatchers.IO) {
        try {
            val response = api.getProductById(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                productDao?.let { dao ->
                    val localProduct = dao.getProductById(id)?.toDomain()
                    if (localProduct != null) Result.success(localProduct)
                    else Result.failure(Exception("Product not found"))
                } ?: Result.failure(Exception("Product not found"))
            }
        } catch (e: Exception) {
            productDao?.let { dao ->
                val localProduct = dao.getProductById(id)?.toDomain()
                if (localProduct != null) Result.success(localProduct)
                else Result.failure(e)
            } ?: Result.failure(e)
        }
    }

    suspend fun createProduct(product: Product): Result<Product> = withContext(Dispatchers.IO) {
        try {
            val response = api.createProduct(product)
            if (response.isSuccessful && response.body() != null) {
                val createdProduct = response.body()!!
                productDao?.insertProduct(com.example.appmovil.data.local.entity.ProductEntity.fromDomain(createdProduct))
                Result.success(createdProduct)
            } else {
                Result.failure(Exception("Error creating product"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProduct(id: Long, product: Product): Result<Product> = withContext(Dispatchers.IO) {
        try {
            val response = api.updateProduct(id, product)
            if (response.isSuccessful && response.body() != null) {
                val updatedProduct = response.body()!!
                productDao?.insertProduct(com.example.appmovil.data.local.entity.ProductEntity.fromDomain(updatedProduct))
                Result.success(updatedProduct)
            } else {
                Result.failure(Exception("Error updating product"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.deleteProduct(id)
            if (response.isSuccessful) {
                productDao?.deleteProductById(id)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar producto"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchProducts(nombre: String): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val response = api.searchProducts(nombre)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                productDao?.let { dao ->
                    val localProducts = dao.searchProducts(nombre).map { it.toDomain() }
                    Result.success(localProducts)
                } ?: Result.failure(Exception("Search failed"))
            }
        } catch (e: Exception) {
            productDao?.let { dao ->
                val localProducts = dao.searchProducts(nombre).map { it.toDomain() }
                Result.success(localProducts)
            } ?: Result.failure(e)
        }
    }

    suspend fun registerOutput(id: Long, cantidad: Int): Result<Product> = withContext(Dispatchers.IO) {
        try {
            val payload = mapOf("id" to id, "cantidad" to cantidad)
            val response = api.registerOutput(payload)
            if (response.isSuccessful && response.body() != null) {
                val updatedProduct = response.body()!!
                productDao?.insertProduct(com.example.appmovil.data.local.entity.ProductEntity.fromDomain(updatedProduct))
                Result.success(updatedProduct)
            } else {
                Result.failure(Exception("Error registering output"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Inventory Methods
    suspend fun getInventoryMovements(): Result<List<InventoryMovement>> = withContext(Dispatchers.IO) {
        try {
            handle(inventoryApi.getAllMovements())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createInventoryMovement(movement: InventoryMovement): Result<InventoryMovement> = withContext(Dispatchers.IO) {
        try {
            handle(inventoryApi.createMovement(movement))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // User Methods
    suspend fun login(username: String, password: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val credentials = mapOf("username" to username, "password" to password)
            val response = userApi.login(credentials)
            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!
                if (loginResponse.error != null) {
                    Result.failure(Exception(loginResponse.error))
                } else {
                    Result.success(loginResponse.message)
                }
            } else {
                Result.failure(Exception("Error en login: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createUser(user: User): Result<User> = withContext(Dispatchers.IO) {
        try {
            handle(userApi.createUser(user))
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

