package com.example.appmovil.domain.model

data class User(
    val id: Long? = null,
    val username: String,
    val password: String,
    val email: String
)
