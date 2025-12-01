package com.example.appmovil.data.repository

import com.example.appmovil.data.local.AppDatabase
import com.example.appmovil.data.local.dao.MovimientoDao
import com.example.appmovil.data.local.dao.ProductoDao
import com.example.appmovil.data.local.entity.Producto
import android.util.Log
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class ProductRepositoryTest {
    
    private lateinit var database: AppDatabase
    private lateinit var productoDao: ProductoDao
    private lateinit var movimientoDao: MovimientoDao
    private lateinit var repository: ProductRepository
    
    @Before
    fun setup() {
        // Mockear Log para evitar errores en tests unitarios
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any<Throwable>()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        
        database = mockk()
        productoDao = mockk()
        movimientoDao = mockk()
        
        every { database.productoDao() } returns productoDao
        every { database.movimientoDao() } returns movimientoDao
        
        repository = ProductRepository(database, productoDao, movimientoDao)
    }
    
    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }
    
    @Test
    fun `getAllProductos debe retornar lista de productos`() = runTest {
        val productosEsperados = listOf(
            Producto("1", "Producto 1", "Desc", 10.0, 5, null),
            Producto("2", "Producto 2", "Desc", 20.0, 10, null)
        )
        
        coEvery { productoDao.getAllProductosSuspend() } returns productosEsperados
        
        val resultado = repository.getAllProductos()
        
        resultado shouldBe productosEsperados
        coVerify { productoDao.getAllProductosSuspend() }
    }
    
    @Test
    fun `insertarProducto debe guardar producto localmente`() = runTest {
        val producto = Producto("EXT-1", "Producto", "Desc", 10.0, 5, null)
        
        coEvery { productoDao.insertarProducto(any()) } just Runs
        coEvery { movimientoDao.insertarMovimiento(any()) } just Runs
        
        // Usar sincronizar = false para evitar llamadas al backend
        repository.insertarProducto(producto, sincronizar = false)
        
        coVerify { productoDao.insertarProducto(producto) }
    }
}

