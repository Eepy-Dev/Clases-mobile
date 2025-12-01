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
    private val productDao: com.example.appmovil.data.local.dao.ProductDao? = null,
    private val deletedProductDao: com.example.appmovil.data.local.dao.DeletedProductDao? = null
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
            // First get the product to save it to history
            val productResult = getProductById(id)
            if (productResult.isSuccess) {
                val product = productResult.getOrNull()
                if (product != null) {
                    deletedProductDao?.insertDeletedProduct(
                        com.example.appmovil.data.local.entity.DeletedProductEntity(
                            originalId = product.id,
                            nombre = product.nombre,
                            precio = product.precio
                        )
                    )
                }
            }

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

    suspend fun getDeletedProducts(): Result<List<com.example.appmovil.data.local.entity.DeletedProductEntity>> = withContext(Dispatchers.IO) {
        try {
            deletedProductDao?.let { dao ->
                Result.success(dao.getAllDeletedProducts())
            } ?: Result.failure(Exception("No local database"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOnlineCatalog(): Result<List<Product>> = withContext(Dispatchers.IO) {
        // Mock data for "Catálogo Online"
        val mockCatalog = listOf(
            Product(id = 101, nombre = "Torta de Chocolate", precio = 15000.0, stock = 10, imagenUrl = "https://example.com/torta.jpg"),
            Product(id = 102, nombre = "Galletas de Almendra", precio = 5000.0, stock = 20, imagenUrl = "https://example.com/galletas.jpg"),
            Product(id = 103, nombre = "Trufas de Chocolate", precio = 8000.0, stock = 15, imagenUrl = "https://example.com/trufas.jpg"),
            Product(id = 104, nombre = "Pie de Limón", precio = 12000.0, stock = 5, imagenUrl = "https://example.com/pie.jpg"),
            Product(id = 105, nombre = "Cupcakes Red Velvet", precio = 2500.0, stock = 30, imagenUrl = "https://example.com/cupcakes.jpg"),
            Product(id = 106, nombre = "Brownie con Nuez", precio = 3000.0, stock = 25, imagenUrl = "https://example.com/brownie.jpg"),
            Product(id = 107, nombre = "Tarta de Frutilla", precio = 14000.0, stock = 8, imagenUrl = "https://example.com/tarta.jpg"),
            Product(id = 108, nombre = "Alfajores Artesanales", precio = 1500.0, stock = 50, imagenUrl = "https://example.com/alfajores.jpg"),
            Product(id = 109, nombre = "Macarons Surtidos", precio = 10000.0, stock = 12, imagenUrl = "https://example.com/macarons.jpg"),
            Product(id = 110, nombre = "Cheesecake de Frambuesa", precio = 18000.0, stock = 6, imagenUrl = "https://example.com/cheesecake.jpg")
        )
        Result.success(mockCatalog)
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


}

