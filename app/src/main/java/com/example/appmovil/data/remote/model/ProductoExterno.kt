package com.example.appmovil.data.remote.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProductoExterno(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("title")
    val nombre: String? = null,
    @SerializedName("description")
    val descripcion: String? = null,
    @SerializedName("price")
    val precio: Double? = null,
    @SerializedName("stock")
    val stock: Int? = null,
    @SerializedName("image")
    val imagenUrl: String? = null
) : Serializable

