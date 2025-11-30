package com.example.appmovil.ui.viewmodel

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("LoginViewModel - Pruebas de Autenticación")
class LoginViewModelTest {

    private lateinit var loginViewModel: LoginViewModel

    @BeforeEach
    fun setUp() {
        loginViewModel = LoginViewModel()
    }

    @Test
    @DisplayName("Debe retornar error cuando el campo usuario está vacío")
    fun `validar login con usuario vacio debe retornar error`() {
        // Given (Arrange)
        val usuarioVacio = ""
        val contrasena = "admin"

        // When (Act)
        val resultado = loginViewModel.validarLogin(usuarioVacio, contrasena)

        // Then (Assert)
        assertEquals(false, resultado.esValido)
        assertEquals(false, resultado.credencialesCorrectas)
        assertTrue(resultado.errores.containsKey("usuario"))
        assertEquals("El campo usuario no puede estar vacío", resultado.errores["usuario"])
        assertEquals(false, loginViewModel.loginResult.value)
    }

    @Test
    @DisplayName("Debe retornar éxito cuando las credenciales son correctas (admin/admin)")
    fun `validar login con credenciales correctas debe retornar exito`() {
        // Given (Arrange)
        val usuarioCorrecto = "admin"
        val contrasenaCorrecta = "admin"

        // When (Act)
        val resultado = loginViewModel.validarLogin(usuarioCorrecto, contrasenaCorrecta)

        // Then (Assert)
        assertEquals(true, resultado.esValido)
        assertEquals(true, resultado.credencialesCorrectas)
        assertTrue(resultado.errores.isEmpty())
        assertEquals(true, loginViewModel.loginResult.value)
    }

    @Test
    @DisplayName("Debe retornar error cuando las credenciales son incorrectas")
    fun `validar login con credenciales incorrectas debe retornar error`() {
        // Given (Arrange)
        val usuarioIncorrecto = "usuario123"
        val contrasenaIncorrecta = "password123"

        // When (Act)
        val resultado = loginViewModel.validarLogin(usuarioIncorrecto, contrasenaIncorrecta)

        // Then (Assert)
        assertEquals(false, resultado.esValido)
        assertEquals(false, resultado.credencialesCorrectas)
        assertTrue(resultado.errores.containsKey("general"))
        assertEquals("Usuario o contraseña incorrectos", resultado.errores["general"])
        assertEquals(false, loginViewModel.loginResult.value)
    }
}

