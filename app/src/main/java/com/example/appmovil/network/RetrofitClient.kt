package com.example.appmovil.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Cliente Retrofit configurado para consumir MockAPI.io (Backend simulado)
 * 
 * Base URL: https://692c2805c829d464006eb028.mockapi.io/
 * Endpoint de productos: /productos
 * 
 * Incluye:
 * - Logging interceptor para debug
 * - Timeout configurado
 * - Conversor Gson para JSON
 */
object RetrofitClient {
    
    // URL base de MockAPI.io para el backend simulado
    private const val BASE_URL_MOCK_API = "https://692c2805c829d464006eb028.mockapi.io/"
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    // Cliente Retrofit para MockAPI.io (Backend simulado)
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL_MOCK_API)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val mockApiService: MockApiService = retrofit.create(MockApiService::class.java)
}

