package com.example.appmovil.util

import com.example.appmovil.data.Producto
import com.example.appmovil.ui.viewmodel.ProductoViewModel

object ProductoValidator {
    fun guardarProducto(
        id: String,
        nombre: String,
        descripcion: String,
        precio: String,
        cantidad: String,
        rutaFoto: String?,
        esModoEdicion: Boolean,
        productoViewModel: ProductoViewModel,
        onMensaje: (String) -> Unit
    ): Boolean {
        // Validaciones
        if (id.trim().isEmpty()) {
            onMensaje("El ID es obligatorio")
            return false
        }
        if (nombre.trim().isEmpty()) {
            onMensaje("El nombre es obligatorio")
            return false
        }
        if (descripcion.trim().isEmpty()) {
            onMensaje("La descripción es obligatoria")
            return false
        }
        if (precio.trim().isEmpty()) {
            onMensaje("El precio es obligatorio")
            return false
        }
        if (cantidad.trim().isEmpty()) {
            onMensaje("La cantidad es obligatoria")
            return false
        }
        
        val precioDouble = precio.toDoubleOrNull()
        val cantidadInt = cantidad.toIntOrNull()
        
        if (precioDouble == null || precioDouble <= 0) {
            onMensaje("El precio debe ser un número válido mayor a 0")
            return false
        }
        if (cantidadInt == null || cantidadInt < 0) {
            onMensaje("La cantidad debe ser un número válido mayor o igual a 0")
            return false
        }
        
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

