package com.example.appmovil.util

import com.example.appmovil.data.Producto
import com.example.appmovil.ui.viewmodel.ProductoViewModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking

class ProductoValidatorTest : FunSpec({

    val mockViewModel = mockk<ProductoViewModel>()

    beforeEach {
        // Configuración inicial para cada prueba
    }

    context("Validación de campos obligatorios") {
        test("debe retornar error cuando el ID está vacío") {
            runBlocking {
                val resultado = ProductoValidator.validarProducto(
                    id = "",
                    nombre = "Producto Test",
                    descripcion = "Descripción",
                    precio = "10.50",
                    cantidad = "5",
                    esModoEdicion = false,
                    productoViewModel = mockViewModel
                )

                resultado.esValido.shouldBeFalse()
                resultado.errores["id"].shouldBe("El ID es obligatorio")
            }
        }

        test("debe retornar error cuando el nombre está vacío") {
            runBlocking {
                coEvery { mockViewModel.existeProductoConId("PROD001") } returns false
                
                val resultado = ProductoValidator.validarProducto(
                    id = "PROD001",
                    nombre = "",
                    descripcion = "Descripción",
                    precio = "10.50",
                    cantidad = "5",
                    esModoEdicion = false,
                    productoViewModel = mockViewModel
                )

                resultado.esValido.shouldBeFalse()
                resultado.errores["nombre"].shouldBe("El nombre es obligatorio")
            }
        }

        test("debe retornar error cuando la descripción está vacía") {
            runBlocking {
                coEvery { mockViewModel.existeProductoConId("PROD001") } returns false
                
                val resultado = ProductoValidator.validarProducto(
                    id = "PROD001",
                    nombre = "Producto Test",
                    descripcion = "",
                    precio = "10.50",
                    cantidad = "5",
                    esModoEdicion = false,
                    productoViewModel = mockViewModel
                )

                resultado.esValido.shouldBeFalse()
                resultado.errores["descripcion"].shouldBe("La descripción es obligatoria")
            }
        }

        test("debe retornar múltiples errores cuando varios campos están vacíos") {
            runBlocking {
                val resultado = ProductoValidator.validarProducto(
                    id = "",
                    nombre = "",
                    descripcion = "",
                    precio = "",
                    cantidad = "",
                    esModoEdicion = false,
                    productoViewModel = mockViewModel
                )

                resultado.esValido.shouldBeFalse()
                resultado.errores.size.shouldBe(5)
                resultado.errores["id"].shouldBe("El ID es obligatorio")
                resultado.errores["nombre"].shouldBe("El nombre es obligatorio")
                resultado.errores["descripcion"].shouldBe("La descripción es obligatoria")
                resultado.errores["precio"].shouldBe("El precio es obligatorio")
                resultado.errores["cantidad"].shouldBe("La cantidad es obligatoria")
            }
        }
    }

    context("Validación de formato numérico") {
        test("debe retornar error cuando el precio no es un número válido") {
            runBlocking {
                coEvery { mockViewModel.existeProductoConId(any()) } returns false
                
                val resultado = ProductoValidator.validarProducto(
                    id = "PROD001",
                    nombre = "Producto Test",
                    descripcion = "Descripción",
                    precio = "precio_invalido",
                    cantidad = "5",
                    esModoEdicion = false,
                    productoViewModel = mockViewModel
                )

                resultado.esValido.shouldBeFalse()
                resultado.errores["precio"].shouldBe("El precio debe ser un número válido mayor a 0")
            }
        }

        test("debe retornar error cuando el precio es menor o igual a cero") {
            runBlocking {
                coEvery { mockViewModel.existeProductoConId(any()) } returns false
                
                val resultado = ProductoValidator.validarProducto(
                    id = "PROD001",
                    nombre = "Producto Test",
                    descripcion = "Descripción",
                    precio = "0",
                    cantidad = "5",
                    esModoEdicion = false,
                    productoViewModel = mockViewModel
                )

                resultado.esValido.shouldBeFalse()
                resultado.errores["precio"].shouldBe("El precio debe ser un número válido mayor a 0")
            }
        }

        test("debe retornar error cuando la cantidad no es un número válido") {
            runBlocking {
                coEvery { mockViewModel.existeProductoConId(any()) } returns false
                
                val resultado = ProductoValidator.validarProducto(
                    id = "PROD001",
                    nombre = "Producto Test",
                    descripcion = "Descripción",
                    precio = "10.50",
                    cantidad = "cantidad_invalida",
                    esModoEdicion = false,
                    productoViewModel = mockViewModel
                )

                resultado.esValido.shouldBeFalse()
                resultado.errores["cantidad"].shouldBe("La cantidad debe ser un número válido mayor o igual a 0")
            }
        }
    }

    context("Validación de ID único") {
        test("debe retornar error cuando el ID ya existe al crear un producto nuevo") {
            runBlocking {
                coEvery { mockViewModel.existeProductoConId("PROD001") } returns true
                
                val resultado = ProductoValidator.validarProducto(
                    id = "PROD001",
                    nombre = "Producto Test",
                    descripcion = "Descripción",
                    precio = "10.50",
                    cantidad = "5",
                    esModoEdicion = false,
                    productoViewModel = mockViewModel
                )

                resultado.esValido.shouldBeFalse()
                resultado.errores["id"]?.shouldBe("El ID 'PROD001' ya existe en el inventario. Por favor, usa otro ID.")
            }
        }

        test("debe permitir crear producto cuando el ID no existe") {
            runBlocking {
                coEvery { mockViewModel.existeProductoConId("PROD001") } returns false
                
                val resultado = ProductoValidator.validarProducto(
                    id = "PROD001",
                    nombre = "Producto Test",
                    descripcion = "Descripción",
                    precio = "10.50",
                    cantidad = "5",
                    esModoEdicion = false,
                    productoViewModel = mockViewModel
                )

                resultado.esValido.shouldBeTrue()
                resultado.errores.isEmpty().shouldBeTrue()
            }
        }

        test("debe permitir editar producto aunque el ID ya exista (modo edición)") {
            runBlocking {
                val resultado = ProductoValidator.validarProducto(
                    id = "PROD001",
                    nombre = "Producto Actualizado",
                    descripcion = "Nueva descripción",
                    precio = "15.75",
                    cantidad = "10",
                    esModoEdicion = true,
                    productoViewModel = mockViewModel
                )

                // En modo edición, no se valida ID único
                resultado.esValido.shouldBeTrue()
            }
        }
    }
})

