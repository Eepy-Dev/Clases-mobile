package com.example.appmovil.utils

object ProductoValidator {
    fun validateNombre(nombre: String): Boolean {
        return nombre.isNotBlank()
    }

    fun validatePrecio(precio: Double): Boolean {
        return precio > 0
    }

    fun validateStock(stock: Int): Boolean {
        return stock >= 0
    }

    fun validateProducto(nombre: String, precio: Double, stock: Int): Boolean {
        return validateNombre(nombre) && validatePrecio(precio) && validateStock(stock)
    }
}
