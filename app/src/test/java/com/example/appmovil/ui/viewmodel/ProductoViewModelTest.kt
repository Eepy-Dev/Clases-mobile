package com.example.appmovil.ui.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.appmovil.data.local.AppDatabase
import com.example.appmovil.data.local.entity.Producto
import com.example.appmovil.data.mapper.ProductMapper
import com.example.appmovil.data.remote.model.ProductoExterno
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ProductoViewModelTest {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    private lateinit var application: Application
    private lateinit var viewModel: ProductoViewModel
    
    @Before
    fun setup() {
        application = mockk(relaxed = true)
        mockkStatic(AppDatabase::class)
        
        // Mockear la base de datos
        val database = mockk<AppDatabase>(relaxed = true)
        every { AppDatabase.getDatabase(any()) } returns database
        
        viewModel = ProductoViewModel(application)
    }
    
    @Test
    fun `convertirProductoExternoALocal debe convertir correctamente`() {
        val productoExterno = ProductoExterno(
            id = 123L,
            nombre = "Torta Chocolate",
            descripcion = "Deliciosa torta",
            precio = 15000.0,
            stock = 10,
            imagenUrl = "http://example.com/image.jpg"
        )
        
        val resultado = viewModel.convertirProductoExternoALocal(productoExterno)
        
        resultado.id shouldBe "EXT-123"
        resultado.nombre shouldBe "Torta Chocolate"
        resultado.descripcion shouldBe "Deliciosa torta"
        resultado.precio shouldBe 15000.0
        resultado.cantidad shouldBe 10
        resultado.foto shouldBe "http://example.com/image.jpg"
    }
    
    @Test
    fun `limpiarMensaje debe limpiar el mensaje`() {
        viewModel.limpiarMensaje()
        
        viewModel.mensaje.value shouldBe ""
    }
    
    @Test
    fun `setImagenCapturada debe establecer la imagen correctamente`() {
        val bitmap = android.graphics.Bitmap.createBitmap(100, 100, android.graphics.Bitmap.Config.ARGB_8888)
        val ruta = "/path/to/image.jpg"
        
        viewModel.setImagenCapturada(bitmap, ruta)
        
        val resultado = viewModel.imagenCapturada.value
        resultado shouldNotBe null
        resultado?.second shouldBe ruta
    }
}

