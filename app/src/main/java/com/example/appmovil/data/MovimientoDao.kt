package com.example.appmovil.data

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MovimientoDao {
    
    @Query("SELECT * FROM movimientos ORDER BY fecha DESC")
    fun getAllMovimientos(): LiveData<List<MovimientoInventario>>
    
    @Query("SELECT * FROM movimientos WHERE productoId = :productoId ORDER BY fecha DESC")
    fun getMovimientosPorProducto(productoId: String): LiveData<List<MovimientoInventario>>
    
    @Query("SELECT * FROM movimientos WHERE tipo = :tipo ORDER BY fecha DESC")
    fun getMovimientosPorTipo(tipo: String): LiveData<List<MovimientoInventario>>
    
    @Query("SELECT * FROM movimientos ORDER BY fecha DESC LIMIT :limite")
    suspend fun getUltimosMovimientos(limite: Int): List<MovimientoInventario>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarMovimiento(movimiento: MovimientoInventario)
    
    @Query("DELETE FROM movimientos")
    suspend fun eliminarTodosLosMovimientos()
}

