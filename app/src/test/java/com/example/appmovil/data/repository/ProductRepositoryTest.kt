package com.example.appmovil.data.repository

import com.example.appmovil.data.local.AppDatabase
import com.example.appmovil.data.local.dao.MovimientoDao
import com.example.appmovil.data.local.dao.ProductoDao
import com.example.appmovil.data.local.entity.MovimientoInventario
import com.example.appmovil.data.local.entity.Producto
import com.example.appmovil.data.remote.model.ProductoExterno
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ProductRepositoryTest {
    
    private lateinit var database: AppDatabase
    private lateinit var productoDao: ProductoDao
    private lateinit var movimientoDao: MovimientoDao
    private lateinit var repository: ProductRepository
    
    @Before
    fun setup() {
        database = mockk()
        productoDao = mockk()
        movimientoDao = mockk()
        
        every { database.productoDao() } returns productoDao
        every { database.movimientoDao() } returns movimientoDao
        
        repository = ProductRepository(database, productoDao, movimientoDao)
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
    fun `getProductoById debe retornar producto cuando existe`() = runTest {
        val producto = Producto("1", "Producto", "Desc", 10.0, 5, null)
        
        coEvery { productoDao.getProductoById("1") } returns producto
        
        val resultado = repository.getProductoById("1")
        
        resultado shouldBe producto
        coVerify { productoDao.getProductoById("1") }
    }
    
    @Test
    fun `buscarProductos debe retornar productos filtrados`() = runTest {
        val productos = listOf(
            Producto("1", "Torta Chocolate", "Desc", 10.0, 5, null)
        )
        
        coEvery { productoDao.buscarProductos("Torta") } returns productos
        
        val resultado = repository.buscarProductos("Torta")
        
        resultado shouldBe productos
        coVerify { productoDao.buscarProductos("Torta") }
    }
    
    @Test
    fun `insertarProducto debe guardar producto localmente`() = runTest {
        val producto = Producto("1", "Producto", "Desc", 10.0, 5, null)
        
        coEvery { productoDao.insertarProducto(any()) } just Runs
        coEvery { movimientoDao.insertarMovimiento(any()) } just Runs
        
        repository.insertarProducto(producto)
        
        coVerify { productoDao.insertarProducto(producto) }
    }
    
    @Test
    fun `actualizarProducto debe actualizar producto localmente`() = runTest {
        val producto = Producto("1", "Producto", "Desc", 10.0, 5, null)
        
        coEvery { productoDao.actualizarProducto(any()) } just Runs
        
        repository.actualizarProducto(producto)
        
        coVerify { productoDao.actualizarProducto(producto) }
    }
    
    @Test
    fun `eliminarProducto debe eliminar producto localmente`() = runTest {
        val producto = Producto("1", "Producto", "Desc", 10.0, 5, null)
        
        coEvery { productoDao.eliminarProducto(any()) } just Runs
        
        repository.eliminarProducto(producto)
        
        coVerify { productoDao.eliminarProducto(producto) }
    }
    
    @Test
    fun `obtenerMovimientos debe retornar lista de movimientos`() = runTest {
        val movimientos = listOf(
            MovimientoInventario(
                productoId = "1",
                nombreProducto = "Producto",
                tipo = "ENTRADA",
                cantidad = 10,
                stockAnterior = 0,
                stockNuevo = 10
            )
        )
        
        coEvery { movimientoDao.getUltimosMovimientos() } returns movimientos
        
        val resultado = repository.obtenerMovimientos()
        
        resultado shouldBe movimientos
        coVerify { movimientoDao.getUltimosMovimientos() }
    }
}

