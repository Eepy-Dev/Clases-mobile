package com.example.appmovil.data.remote

import retrofit2.http.GET

data class DogResponse(
    val message: String,
    val status: String
)

interface ExternalApiService {
    @GET("breeds/image/random")
    suspend fun getRandomDogImage(): DogResponse
}
