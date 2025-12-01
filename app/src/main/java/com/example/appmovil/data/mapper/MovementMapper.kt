package com.example.appmovil.data.mapper

import com.example.appmovil.data.local.entity.MovimientoInventario
import com.example.appmovil.data.remote.model.MovimientoBackend

object MovementMapper {
    
    fun toDomain(movimientoLocal: MovimientoInventario): MovimientoBackend? {
        // Normalizar el tipo de movimiento
        val tipoNormalizado = when {
            movimientoLocal.tipo.startsWith("SALIDA") -> "SALIDA"
            movimientoLocal.tipo == "ENTRADA" -> "ENTRADA"
            else -> movimientoLocal.tipo
        }
        
        // Extraer ID numérico del producto (debe ser un número válido, no EXT- ni PROD-)
        val productoId = extractNumericId(movimientoLocal.productoId)
        
        // Si no se puede extraer un ID válido, retornar null
        if (productoId == null) {
            return null
        }
        
        return MovimientoBackend(
            id = null,
            productoId = productoId,
            tipo = tipoNormalizado,
            cantidad = movimientoLocal.cantidad,
            stockAnterior = movimientoLocal.stockAnterior,
            stockNuevo = movimientoLocal.stockNuevo,
            fecha = null // El backend genera la fecha automáticamente
        )
    }
    
    fun toLocal(movimientoBackend: MovimientoBackend, productoId: String, nombreProducto: String): MovimientoInventario {
        return MovimientoInventario(
            id = movimientoBackend.id ?: 0L,
            productoId = productoId,
            nombreProducto = nombreProducto,
            tipo = movimientoBackend.tipo,
            cantidad = movimientoBackend.cantidad,
            stockAnterior = movimientoBackend.stockAnterior,
            stockNuevo = movimientoBackend.stockNuevo,
            fecha = System.currentTimeMillis()
        )
    }
    
    private fun extractNumericId(id: String): Long? {
        return when {
            id.startsWith("EXT-") -> null // Los productos EXT- no se sincronizan
            id.startsWith("PROD-") -> null // Los productos PROD- aún no tienen ID del backend, no sincronizar movimientos
            else -> id.toLongOrNull() // Solo IDs numéricos directos del backend
        }
    }
}

