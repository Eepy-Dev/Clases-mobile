package com.example.appmovil.util

import com.example.appmovil.data.Producto
import com.example.appmovil.ui.viewmodel.ProductoViewModel

object ProductoValidator {
    suspend fun validarProducto(
        id: String,
        nombre: String,
        descripcion: String,
        precio: String,
        cantidad: String,
        esModoEdicion: Boolean,
        productoViewModel: ProductoViewModel
    ): ValidationResult {
        val errores = mutableMapOf<String, String>()
        
        // Validar ID
        if (id.trim().isEmpty()) {
            errores["id"] = "El ID es obligatorio"
        } else if (!esModoEdicion) {
            // Validar ID único solo al crear (no en edición)
            val idExiste = productoViewModel.existeProductoConId(id.trim())
            if (idExiste) {
                errores["id"] = "El ID '${id.trim()}' ya existe en el inventario. Por favor, usa otro ID."
            }
        }
        
        // Validar nombre
        if (nombre.trim().isEmpty()) {
            errores["nombre"] = "El nombre es obligatorio"
        }
        
        // Validar descripción
        if (descripcion.trim().isEmpty()) {
            errores["descripcion"] = "La descripción es obligatoria"
        }
        
        // Validar precio
        if (precio.trim().isEmpty()) {
            errores["precio"] = "El precio es obligatorio"
        } else {
            val precioDouble = precio.toDoubleOrNull()
            if (precioDouble == null || precioDouble <= 0) {
                errores["precio"] = "El precio debe ser un número válido mayor a 0"
            }
        }
        
        // Validar cantidad
        if (cantidad.trim().isEmpty()) {
            errores["cantidad"] = "La cantidad es obligatoria"
        } else {
            val cantidadInt = cantidad.toIntOrNull()
            if (cantidadInt == null || cantidadInt < 0) {
                errores["cantidad"] = "La cantidad debe ser un número válido mayor o igual a 0"
            }
        }
        
        return ValidationResult(errores, errores.isEmpty())
    }
    
    suspend fun guardarProducto(
        id: String,
        nombre: String,
        descripcion: String,
        precio: String,
        cantidad: String,
        rutaFoto: String?,
        esModoEdicion: Boolean,
        productoViewModel: ProductoViewModel
    ): Boolean {
        // Primero validar
        val resultado = validarProducto(id, nombre, descripcion, precio, cantidad, esModoEdicion, productoViewModel)
        
        // Si hay errores, no guardar
        if (!resultado.esValido) {
            return false
        }
        
        // Si todo está bien, crear y guardar el producto
        val precioDouble = precio.toDoubleOrNull() ?: 0.0
        val cantidadInt = cantidad.toIntOrNull() ?: 0
        
        val producto = Producto(
            id = id.trim(),
            nombre = nombre.trim(),
            descripcion = descripcion.trim(),
            precio = precioDouble,
            cantidad = cantidadInt,
            foto = rutaFoto
        )
        
        if (esModoEdicion) {
            productoViewModel.actualizarProducto(producto)
        } else {
            productoViewModel.insertarProducto(producto)
        }
        
        return true
    }
}

