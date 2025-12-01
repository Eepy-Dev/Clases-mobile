package com.example.appmovil.data.remote.model

data class LoginResponse(
    val message: String,
    val username: String? = null,
    val error: String? = null
)
