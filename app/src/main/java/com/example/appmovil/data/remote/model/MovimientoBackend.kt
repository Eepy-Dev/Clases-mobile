package com.example.appmovil.data.remote.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MovimientoBackend(
    val id: Long? = null,
    val productoId: Long,
    val tipo: String, // ENTRADA, SALIDA
    val cantidad: Int,
    val stockAnterior: Int,
    val stockNuevo: Int,
    val fecha: String? = null // El backend genera la fecha automáticamente
) : Serializable

