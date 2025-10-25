package com.example.appmovil

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey
    val id: String,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val cantidad: Int,
    val foto: String? = null // Ruta de la foto o null si no tiene
)
