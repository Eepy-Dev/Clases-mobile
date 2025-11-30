package com.example.appmovil.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val PRODUCT_SERVICE_URL = "http://10.0.2.2:8081/"
    private const val INVENTORY_SERVICE_URL = "http://10.0.2.2:8082/"
    private const val USER_SERVICE_URL = "http://10.0.2.2:8083/"
    private const val EXTERNAL_API_URL = "https://dog.ceo/api/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val productApiService: ProductApiService by lazy {
        Retrofit.Builder()
            .baseUrl(PRODUCT_SERVICE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ProductApiService::class.java)
    }

    val inventoryApiService: InventoryApiService by lazy {
        Retrofit.Builder()
            .baseUrl(INVENTORY_SERVICE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(InventoryApiService::class.java)
    }

    val userApiService: UserApiService by lazy {
        Retrofit.Builder()
            .baseUrl(USER_SERVICE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(UserApiService::class.java)
    }

    val externalApiService: ExternalApiService by lazy {
        Retrofit.Builder()
            .baseUrl(EXTERNAL_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ExternalApiService::class.java)
    }
}

