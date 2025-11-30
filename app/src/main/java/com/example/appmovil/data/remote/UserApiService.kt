package com.example.appmovil.data.remote

import com.example.appmovil.domain.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApiService {
    @POST("usuarios")
    suspend fun createUser(@Body user: User): Response<User>

    @POST("usuarios/login")
    suspend fun login(@Body credentials: Map<String, String>): Response<String>
}
