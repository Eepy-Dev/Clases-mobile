package com.example.appmovil.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File

object ImageUtils {
    fun cargarImagen(ruta: String): Bitmap? {
        val archivo = File(ruta)
        return if (archivo.exists()) {
            BitmapFactory.decodeFile(ruta)
        } else {
            null
        }
    }
}

