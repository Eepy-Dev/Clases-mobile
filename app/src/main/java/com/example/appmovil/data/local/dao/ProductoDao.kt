package com.example.appmovil.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.appmovil.data.local.entity.Producto

@Dao
interface ProductoDao {
    
    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun getAllProductos(): LiveData<List<Producto>>
    
    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    suspend fun getAllProductosSuspend(): List<Producto>
    
    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun getProductoById(id: String): Producto?
    
    @Query("SELECT * FROM productos WHERE nombre LIKE '%' || :nombre || '%' OR id LIKE '%' || :nombre || '%'")
    suspend fun buscarProductos(nombre: String): List<Producto>
    
    @Query("SELECT * FROM productos ORDER BY rowid DESC LIMIT :limit")
    suspend fun getUltimosProductos(limit: Int = 10): List<Producto>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProducto(producto: Producto)
    
    @Update
    suspend fun actualizarProducto(producto: Producto)
    
    @Delete
    suspend fun eliminarProducto(producto: Producto)
    
    @Query("DELETE FROM productos WHERE id = :id")
    suspend fun eliminarProductoPorId(id: String)
}

