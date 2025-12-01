package com.example.appmovil.data.mapper

import com.example.appmovil.data.local.entity.MovimientoInventario
import com.example.appmovil.data.remote.model.MovimientoBackend
import io.kotest.matchers.shouldBe
import org.junit.Test

class MovementMapperTest {
    
    @Test
    fun `toDomain debe normalizar tipo SALIDA_VENTA a SALIDA`() {
        val movimientoLocal = MovimientoInventario(
            productoId = "EXT-123",
            nombreProducto = "Producto",
            tipo = "SALIDA_VENTA",
            cantidad = 5,
            stockAnterior = 10,
            stockNuevo = 5
        )
        
        val resultado = MovementMapper.toDomain(movimientoLocal)
        
        resultado.tipo shouldBe "SALIDA"
        resultado.productoId shouldBe 123L
        resultado.cantidad shouldBe 5
    }
    
    @Test
    fun `toDomain debe extraer ID numérico de EXT-`() {
        val movimientoLocal = MovimientoInventario(
            productoId = "EXT-456",
            nombreProducto = "Producto",
            tipo = "ENTRADA",
            cantidad = 10,
            stockAnterior = 0,
            stockNuevo = 10
        )
        
        val resultado = MovementMapper.toDomain(movimientoLocal)
        
        resultado.productoId shouldBe 456L
    }
    
    @Test
    fun `toDomain debe extraer ID numérico de PROD-`() {
        val movimientoLocal = MovimientoInventario(
            productoId = "PROD-789",
            nombreProducto = "Producto",
            tipo = "ENTRADA",
            cantidad = 10,
            stockAnterior = 0,
            stockNuevo = 10
        )
        
        val resultado = MovementMapper.toDomain(movimientoLocal)
        
        resultado.productoId shouldBe 789L
    }
    
    @Test
    fun `toDomain debe mantener tipo ENTRADA`() {
        val movimientoLocal = MovimientoInventario(
            productoId = "1",
            nombreProducto = "Producto",
            tipo = "ENTRADA",
            cantidad = 10,
            stockAnterior = 0,
            stockNuevo = 10
        )
        
        val resultado = MovementMapper.toDomain(movimientoLocal)
        
        resultado.tipo shouldBe "ENTRADA"
    }
    
    @Test
    fun `toDomain debe normalizar SALIDA_MERMA a SALIDA`() {
        val movimientoLocal = MovimientoInventario(
            productoId = "EXT-123",
            nombreProducto = "Producto",
            tipo = "SALIDA_MERMA",
            cantidad = 2,
            stockAnterior = 10,
            stockNuevo = 8
        )
        
        val resultado = MovementMapper.toDomain(movimientoLocal)
        
        resultado.tipo shouldBe "SALIDA"
    }
    
    @Test
    fun `toLocal debe convertir MovimientoBackend a MovimientoInventario`() {
        val movimientoBackend = MovimientoBackend(
            id = 1L,
            productoId = 123L,
            tipo = "ENTRADA",
            cantidad = 10,
            stockAnterior = 0,
            stockNuevo = 10,
            fecha = null
        )
        
        val resultado = MovementMapper.toLocal(movimientoBackend, "EXT-123", "Producto")
        
        resultado.productoId shouldBe "EXT-123"
        resultado.nombreProducto shouldBe "Producto"
        resultado.tipo shouldBe "ENTRADA"
        resultado.cantidad shouldBe 10
    }
}

