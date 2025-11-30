package com.example.appmovil.data.repository

import retrofit2.http.GET

interface ExternalApiService {
    @GET("breeds/image/random")
    suspend fun getRandomDogImage(): DogImageResponse
}

data class DogImageResponse(
    val message: String,
    val status: String
)
