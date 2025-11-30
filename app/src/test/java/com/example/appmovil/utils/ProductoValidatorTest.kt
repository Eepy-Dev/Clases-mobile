package com.example.appmovil.utils

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductoValidatorTest {

    @Test
    fun `validateNombre returns true for valid name`() {
        assertTrue(ProductoValidator.validateNombre("Chocolate"))
    }

    @Test
    fun `validateNombre returns false for empty name`() {
        assertFalse(ProductoValidator.validateNombre(""))
    }

    @Test
    fun `validatePrecio returns true for positive price`() {
        assertTrue(ProductoValidator.validatePrecio(10.0))
    }

    @Test
    fun `validatePrecio returns false for zero or negative price`() {
        assertFalse(ProductoValidator.validatePrecio(0.0))
        assertFalse(ProductoValidator.validatePrecio(-5.0))
    }

    @Test
    fun `validateStock returns true for non-negative stock`() {
        assertTrue(ProductoValidator.validateStock(0))
        assertTrue(ProductoValidator.validateStock(10))
    }

    @Test
    fun `validateStock returns false for negative stock`() {
        assertFalse(ProductoValidator.validateStock(-1))
    }

    @Test
    fun `validateProducto returns true when all fields are valid`() {
        assertTrue(ProductoValidator.validateProducto("Chocolate", 10.0, 5))
    }

    @Test
    fun `validateProducto returns false when any field is invalid`() {
        assertFalse(ProductoValidator.validateProducto("", 10.0, 5))
        assertFalse(ProductoValidator.validateProducto("Chocolate", 0.0, 5))
        assertFalse(ProductoValidator.validateProducto("Chocolate", 10.0, -1))
    }
}
