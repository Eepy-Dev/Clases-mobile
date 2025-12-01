package com.example.appmovil.data.mapper

import com.example.appmovil.data.local.entity.Producto
import com.example.appmovil.data.remote.model.ProductoBackend
import com.example.appmovil.data.remote.model.ProductoExterno
import io.kotest.matchers.shouldBe
import org.junit.Test

class ProductMapperTest {
    
    @Test
    fun `toLocal debe convertir ProductoExterno a Producto con prefijo EXT-`() {
        val productoExterno = ProductoExterno(
            id = 123L,
            nombre = "Torta Chocolate",
            descripcion = "Deliciosa torta",
            precio = 15000.0,
            stock = 10,
            imagenUrl = "http://example.com/image.jpg"
        )
        
        val resultado = ProductMapper.toLocal(productoExterno)
        
        resultado.id shouldBe "EXT-123"
        resultado.nombre shouldBe "Torta Chocolate"
        resultado.descripcion shouldBe "Deliciosa torta"
        resultado.precio shouldBe 15000.0
        resultado.cantidad shouldBe 10
        resultado.foto shouldBe "http://example.com/image.jpg"
    }
    
    @Test
    fun `toBackend debe extraer ID numérico de productos EXT-`() {
        val producto = Producto(
            id = "EXT-456",
            nombre = "Torta",
            descripcion = "Desc",
            precio = 10000.0,
            cantidad = 5,
            foto = "url"
        )
        
        val resultado = ProductMapper.toBackend(producto)
        
        resultado.id shouldBe 456L
        resultado.nombre shouldBe "Torta"
    }
    
    @Test
    fun `toBackend debe retornar null para productos PROD-`() {
        val producto = Producto(
            id = "PROD-12345",
            nombre = "Torta",
            descripcion = "Desc",
            precio = 10000.0,
            cantidad = 5,
            foto = "url"
        )
        
        val resultado = ProductMapper.toBackend(producto)
        
        resultado.id shouldBe null
    }
    
    @Test
    fun `fromBackend debe convertir ProductoBackend a Producto`() {
        val productoBackend = ProductoBackend(
            id = 789L,
            nombre = "Torta",
            descripcion = "Desc",
            precio = 12000.0,
            stock = 8,
            imagenUrl = "url"
        )
        
        val resultado = ProductMapper.fromBackend(productoBackend)
        
        resultado.id shouldBe "789"
        resultado.nombre shouldBe "Torta"
        resultado.cantidad shouldBe 8
    }
    
    @Test
    fun `toLocalList debe convertir lista de ProductosExternos`() {
        val productosExternos = listOf(
            ProductoExterno(id = 1L, nombre = "Producto 1", descripcion = "", precio = 10.0, stock = 5),
            ProductoExterno(id = 2L, nombre = "Producto 2", descripcion = "", precio = 20.0, stock = 10)
        )
        
        val resultado = ProductMapper.toLocalList(productosExternos)
        
        resultado.size shouldBe 2
        resultado[0].id shouldBe "EXT-1"
        resultado[1].id shouldBe "EXT-2"
    }
    
    @Test
    fun `fromBackendList debe convertir lista de ProductosBackend`() {
        val productosBackend = listOf(
            ProductoBackend(id = 1L, nombre = "Producto 1", descripcion = "", precio = 10.0, stock = 5),
            ProductoBackend(id = 2L, nombre = "Producto 2", descripcion = "", precio = 20.0, stock = 10)
        )
        
        val resultado = ProductMapper.fromBackendList(productosBackend)
        
        resultado.size shouldBe 2
        resultado[0].id shouldBe "1"
        resultado[1].id shouldBe "2"
    }
}

