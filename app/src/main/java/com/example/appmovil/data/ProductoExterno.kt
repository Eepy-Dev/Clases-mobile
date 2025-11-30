package com.example.appmovil.data

import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos para productos de pastelería del backend simulado (MockAPI.io)
 * Representa los productos obtenidos desde el catálogo online
 */
data class ProductoExterno(
    // Para MockAPI.io, el ID puede ser String o Int
    val id: String? = null,
    @SerializedName("idInt")
    val idInt: Int? = null,
    val title: String,
    val price: Double,
    val description: String,
    val category: String? = null, // Opcional - no se usa en el inventario local
    @SerializedName("image")
    val imagenUrl: String? = null,
    val rating: Rating? = null
) {
    // Función helper para obtener el ID como String (necesario para nuestro sistema)
    fun getIdAsString(): String {
        return id ?: idInt?.toString() ?: ""
    }
}

data class Rating(
    val rate: Double,
    val count: Int
)

