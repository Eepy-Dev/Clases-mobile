package com.example.appmovil.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deleted_products")
data class DeletedProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val originalId: Long,
    val nombre: String,
    val precio: Double,
    val deletedAt: Long = System.currentTimeMillis()
)
