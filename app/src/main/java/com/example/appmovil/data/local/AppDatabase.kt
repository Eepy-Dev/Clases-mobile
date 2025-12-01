package com.example.appmovil.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.appmovil.data.local.dao.MovimientoDao
import com.example.appmovil.data.local.dao.ProductoDao
import com.example.appmovil.data.local.entity.MovimientoInventario
import com.example.appmovil.data.local.entity.Producto

@Database(
    entities = [Producto::class, MovimientoInventario::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun productoDao(): ProductoDao
    abstract fun movimientoDao(): MovimientoDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "productos_database"
                )
                .fallbackToDestructiveMigration() // Permite recrear la BD si cambia el esquema
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

