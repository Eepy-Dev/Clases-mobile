package com.example.appmovil.domain.model

data class Product(
    val id: Long? = null,
    val nombre: String,
    val precio: Double,
    val stock: Int,
    val imagenUrl: String? = null
)
