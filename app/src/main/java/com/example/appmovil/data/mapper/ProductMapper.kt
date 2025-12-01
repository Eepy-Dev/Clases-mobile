package com.example.appmovil.data.mapper

import com.example.appmovil.data.local.entity.Producto
import com.example.appmovil.data.remote.model.ProductoBackend
import com.example.appmovil.data.remote.model.ProductoExterno

object ProductMapper {
    
    fun toLocal(productoExterno: ProductoExterno): Producto {
        return Producto(
            id = "EXT-${productoExterno.id ?: System.currentTimeMillis()}",
            nombre = productoExterno.nombre ?: "Sin nombre",
            descripcion = productoExterno.descripcion ?: "",
            precio = productoExterno.precio ?: 0.0,
            cantidad = productoExterno.stock ?: 0,
            foto = productoExterno.imagenUrl
        )
    }
    
    fun toBackend(producto: Producto): ProductoBackend {
        // Si el producto tiene prefijo PROD-, enviar id = null para que el backend genere un nuevo ID
        // Solo usar el ID numérico si el producto ya tiene un ID numérico directo (sin prefijo)
        val id = when {
            producto.id.startsWith("PROD-") -> null // Crear nuevo producto en backend
            producto.id.startsWith("EXT-") -> null // No sincronizar
            else -> producto.id.toLongOrNull() // ID numérico directo del backend
        }
        
        return ProductoBackend(
            id = id,
            nombre = producto.nombre,
            descripcion = producto.descripcion,
            precio = producto.precio,
            stock = producto.cantidad,
            imagenUrl = producto.foto
        )
    }
    
    fun fromBackend(productoBackend: ProductoBackend): Producto {
        return Producto(
            id = productoBackend.id?.toString() ?: "PROD-${System.currentTimeMillis()}",
            nombre = productoBackend.nombre,
            descripcion = productoBackend.descripcion,
            precio = productoBackend.precio,
            cantidad = productoBackend.stock,
            foto = productoBackend.imagenUrl
        )
    }
    
    fun extractNumericId(id: String): Long? {
        return when {
            id.startsWith("EXT-") -> null // Los productos EXT- no se sincronizan
            id.startsWith("PROD-") -> {
                // Extraer el número después de "PROD-", ej: "PROD-1" -> 1
                val numero = id.substring(5).toLongOrNull()
                numero
            }
            else -> id.toLongOrNull() // IDs numéricos directos (del backend)
        }
    }
    
    fun toLocalList(productosExternos: List<ProductoExterno>): List<Producto> {
        return productosExternos.map { toLocal(it) }
    }
    
    fun fromBackendList(productosBackend: List<ProductoBackend>): List<Producto> {
        return productosBackend.map { fromBackend(it) }
    }
}

