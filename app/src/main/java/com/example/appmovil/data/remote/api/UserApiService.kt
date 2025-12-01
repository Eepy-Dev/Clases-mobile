package com.example.appmovil.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(
    val usuario: String,
    val contrasena: String
)

data class LoginResponse(
    val success: Boolean,
    val mensaje: String
)

interface UserApiService {
    
    @POST("usuarios/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}

