package com.example.appmovil.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "movimientos")
data class MovimientoInventario(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productoId: String,
    val nombreProducto: String,
    val tipo: String, // "ENTRADA", "SALIDA", "VENTA"
    val cantidad: Int,
    val cantidadAnterior: Int,
    val cantidadNueva: Int,
    val fecha: Long // Timestamp
) : Serializable

