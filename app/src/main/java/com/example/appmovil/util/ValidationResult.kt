package com.example.appmovil.util

/**
 * Resultado de validación que contiene todos los errores encontrados
 * @param errores Mapa de errores donde la clave es el nombre del campo y el valor es el mensaje de error
 * @param esValido true si no hay errores, false si hay al menos un error
 */
data class ValidationResult(
    val errores: Map<String, String> = emptyMap(),
    val esValido: Boolean = errores.isEmpty()
)

