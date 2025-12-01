package com.example.appmovil.data.mapper

import com.example.appmovil.data.local.entity.MovimientoInventario
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.Test

class MovementMapperTest {
    
    @Test
    fun `toDomain debe normalizar tipo SALIDA_VENTA a SALIDA`() {
        val movimientoLocal = MovimientoInventario(
            productoId = "123",
            nombreProducto = "Producto",
            tipo = "SALIDA_VENTA",
            cantidad = 5,
            stockAnterior = 10,
            stockNuevo = 5
        )
        
        val resultado = MovementMapper.toDomain(movimientoLocal)
        
        resultado shouldNotBe null
        resultado!!.tipo shouldBe "SALIDA"
        resultado.productoId shouldBe 123L
        resultado.cantidad shouldBe 5
    }
}

