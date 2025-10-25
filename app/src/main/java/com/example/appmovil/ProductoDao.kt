package com.example.appmovil

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ProductoDao {
    
    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun getAllProductos(): LiveData<List<Producto>>
    
    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun getProductoById(id: String): Producto?
    
    @Query("SELECT * FROM productos WHERE nombre LIKE '%' || :nombre || '%' OR id LIKE '%' || :nombre || '%'")
    suspend fun buscarProductos(nombre: String): List<Producto>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProducto(producto: Producto)
    
    @Update
    suspend fun actualizarProducto(producto: Producto)
    
    @Delete
    suspend fun eliminarProducto(producto: Producto)
    
    @Query("DELETE FROM productos WHERE id = :id")
    suspend fun eliminarProductoPorId(id: String)
}
