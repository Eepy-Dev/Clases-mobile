package com.example.appmovil.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.appmovil.data.local.dao.DeletedProductDao
import com.example.appmovil.data.local.entity.DeletedProductEntity

@Database(entities = [ProductEntity::class, DeletedProductEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun deletedProductDao(): DeletedProductDao
}
