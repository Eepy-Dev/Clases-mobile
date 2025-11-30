package com.example.appmovil.data.remote

import com.example.appmovil.domain.model.InventoryMovement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface InventoryApiService {
    @GET("movimientos")
    suspend fun getAllMovements(): Response<List<InventoryMovement>>

    @GET("movimientos/producto/{id}")
    suspend fun getMovementsByProductId(@Path("id") productId: Long): Response<List<InventoryMovement>>

    @POST("movimientos")
    suspend fun createMovement(@Body movement: InventoryMovement): Response<InventoryMovement>
}
