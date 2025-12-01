package com.example.appmovil.data.remote.api

import com.example.appmovil.data.remote.model.MovimientoBackend
import retrofit2.Response
import retrofit2.http.*

interface InventoryApiService {
    
    @GET("movimientos")
    suspend fun getAllMovimientos(): Response<List<MovimientoBackend>>
    
    @GET("movimientos/producto/{id}")
    suspend fun getMovimientosByProducto(@Path("id") id: Long): Response<List<MovimientoBackend>>
    
    @POST("movimientos")
    suspend fun crearMovimiento(@Body movimiento: MovimientoBackend): Response<MovimientoBackend>
}

