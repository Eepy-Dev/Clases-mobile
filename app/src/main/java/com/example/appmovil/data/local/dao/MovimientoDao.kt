package com.example.appmovil.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.appmovil.data.local.entity.MovimientoInventario

@Dao
interface MovimientoDao {
    
    @Query("SELECT * FROM movimientos ORDER BY fecha DESC")
    fun getAllMovimientos(): LiveData<List<MovimientoInventario>>
    
    @Query("SELECT * FROM movimientos WHERE tipo LIKE :tipoFilter ORDER BY fecha DESC")
    fun getMovimientosByTipo(tipoFilter: String): LiveData<List<MovimientoInventario>>
    
    @Query("SELECT * FROM movimientos WHERE productoId = :productoId ORDER BY fecha DESC")
    suspend fun getMovimientosByProducto(productoId: String): List<MovimientoInventario>
    
    @Insert
    suspend fun insertarMovimiento(movimiento: MovimientoInventario)
    
    @Delete
    suspend fun eliminarMovimiento(movimiento: MovimientoInventario)
    
    @Query("SELECT * FROM movimientos ORDER BY fecha DESC LIMIT 10")
    suspend fun getUltimosMovimientos(): List<MovimientoInventario>
}

