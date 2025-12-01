package com.example.appmovil.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.appmovil.data.local.entity.DeletedProductEntity

@Dao
interface DeletedProductDao {
    @Query("SELECT * FROM deleted_products ORDER BY deletedAt DESC")
    suspend fun getAllDeletedProducts(): List<DeletedProductEntity>

    @Insert
    suspend fun insertDeletedProduct(product: DeletedProductEntity)
}
