package com.example.appmovil.domain.model

import java.time.LocalDateTime

data class InventoryMovement(
    val id: Long? = null,
    val productoId: Long,
    val tipo: String, // "ENTRADA" or "SALIDA"
    val cantidad: Int,
    val fecha: String? = null // ISO date string
)
