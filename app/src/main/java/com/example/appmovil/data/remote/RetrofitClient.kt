package com.example.appmovil.data.remote

import com.example.appmovil.data.remote.api.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    
    // URLs base de los servicios
    private const val BASE_URL_PRODUCTS = "http://10.0.2.2:8081/"
    private const val BASE_URL_INVENTORY = "http://10.0.2.2:8082/"
    private const val BASE_URL_USERS = "http://10.0.2.2:8083/"
    private const val BASE_URL_MOCK_API = "https://692c2805c829d464006eb028.mockapi.io/"
    
    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    // Interceptor para capturar errores de conexión
    private val errorInterceptor = okhttp3.Interceptor { chain ->
        val request = chain.request()
        try {
            val response = chain.proceed(request)
            if (!response.isSuccessful) {
                android.util.Log.e("RetrofitClient", "❌ Error HTTP ${response.code} para ${request.url}")
            }
            response
        } catch (e: Exception) {
            android.util.Log.e("RetrofitClient", "❌ Error de conexión para ${request.url}: ${e.message}")
            throw e
        }
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(errorInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofitProducts = Retrofit.Builder()
        .baseUrl(BASE_URL_PRODUCTS)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    
    private val retrofitInventory = Retrofit.Builder()
        .baseUrl(BASE_URL_INVENTORY)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    
    private val retrofitUsers = Retrofit.Builder()
        .baseUrl(BASE_URL_USERS)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    
    private val retrofitMockApi = Retrofit.Builder()
        .baseUrl(BASE_URL_MOCK_API)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    
    val productsApiService: ProductsApiService = retrofitProducts.create(ProductsApiService::class.java)
    val inventoryApiService: InventoryApiService = retrofitInventory.create(InventoryApiService::class.java)
    val userApiService: UserApiService = retrofitUsers.create(UserApiService::class.java)
    val mockApiService: MockApiService = retrofitMockApi.create(MockApiService::class.java)
}

