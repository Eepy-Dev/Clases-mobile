package com.example.appmovil.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    private lateinit var viewModel: LoginViewModel
    
    @Before
    fun setup() {
        viewModel = LoginViewModel()
    }
    
    @Test
    fun `cuando el usuario está vacío, debe mostrar error`() = runTest {
        viewModel.validarLogin("", "admin")
        
        viewModel.loginResult.value shouldBe false
        viewModel.errorUsuario.value shouldBe "El campo usuario no puede estar vacío"
    }
    
    @Test
    fun `cuando la contraseña está vacía, debe mostrar error`() = runTest {
        viewModel.validarLogin("admin", "")
        
        viewModel.loginResult.value shouldBe false
        viewModel.errorContrasena.value shouldBe "El campo contraseña no puede estar vacío"
    }
    
    @Test
    fun `cuando las credenciales son correctas, debe permitir login`() = runTest {
        viewModel.validarLogin("admin", "admin")
        
        viewModel.loginResult.value shouldBe true
        viewModel.errorUsuario.value shouldBe ""
        viewModel.errorContrasena.value shouldBe ""
    }
    
    @Test
    fun `cuando las credenciales son incorrectas, debe mostrar error`() = runTest {
        viewModel.validarLogin("admin", "wrong")
        
        viewModel.loginResult.value shouldBe false
        viewModel.errorContrasena.value shouldBe "Usuario o contraseña incorrectos"
    }
}

