package com.example.appmovil.data.remote.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProductoBackend(
    val id: Long? = null,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int,
    @SerializedName("imagenUrl")
    val imagenUrl: String? = null
) : Serializable

