package com.example.appmovil.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "movimientos")
data class MovimientoInventario(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productoId: String,
    val nombreProducto: String,
    val tipo: String, // ENTRADA, SALIDA_VENTA, SALIDA_MERMA, SALIDA_MOVIMIENTO
    val cantidad: Int,
    val stockAnterior: Int,
    val stockNuevo: Int,
    val fecha: Long = System.currentTimeMillis()
) : Serializable

