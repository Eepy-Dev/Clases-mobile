package com.example.appmovil.data.mapper

import com.example.appmovil.data.remote.model.ProductoExterno
import io.kotest.matchers.shouldBe
import org.junit.Test

class ProductMapperTest {
    
    @Test
    fun `toLocal debe convertir ProductoExterno a Producto con prefijo EXT-`() {
        val productoExterno = ProductoExterno(
            id = "123",
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
}

