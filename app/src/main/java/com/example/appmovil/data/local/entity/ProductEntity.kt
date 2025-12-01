package com.example.appmovil.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.appmovil.domain.model.Product

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val nombre: String,
    val precio: Double,
    val stock: Int,
    val imagenUrl: String?
) {
    fun toDomain(): Product {
        return Product(
            id = id,
            nombre = nombre,
            precio = precio,
            stock = stock,
            imagenUrl = imagenUrl
        )
    }

    companion object {
        fun fromDomain(product: Product): ProductEntity {
            return ProductEntity(
                id = product.id ?: 0, // Assuming 0 or handling null appropriately if ID is missing for local creation before sync
                nombre = product.nombre,
                precio = product.precio,
                stock = product.stock,
                imagenUrl = product.imagenUrl
            )
        }
    }
}
