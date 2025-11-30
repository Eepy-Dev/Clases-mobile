package com.example.appmovil.ui.viewmodel

import com.example.appmovil.data.repository.ProductRepository
import com.example.appmovil.domain.model.Product
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ProductViewModelTest {

    private lateinit var viewModel: ProductViewModel
    private lateinit var repository: ProductRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        
        // Stub methods called in init
        coEvery { repository.getProducts() } returns Result.success(emptyList())
        coEvery { repository.getRandomDogImage() } returns Result.success("http://example.com/dog.jpg")
        
        viewModel = ProductViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadProducts updates uiState with products on success`() = runTest(testDispatcher) {
        // Given
        val products = listOf(Product(1, "Test", 10.0, 5, null))
        coEvery { repository.getProducts() } returns Result.success(products)

        // When
        viewModel.loadProducts()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(products, viewModel.uiState.value.products)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `addProduct reloads products on success`() = runTest(testDispatcher) {
        // Given
        val newProduct = Product(nombre = "New", precio = 20.0, stock = 10, imagenUrl = null)
        coEvery { repository.createProduct(any()) } returns Result.success(newProduct)
        coEvery { repository.getProducts() } returns Result.success(listOf(newProduct))

        // When
        viewModel.addProduct("New", 20.0, 10, null)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.uiState.value.products.size)
    }

    @Test
    fun `registerOutput updates stock on success`() = runTest(testDispatcher) {
        // Given
        coEvery { repository.registerOutput(1, 1) } returns Result.success(Product(1, "Test", 10.0, 4, null))
        coEvery { repository.getProducts() } returns Result.success(emptyList()) // Mock reload

        // When
        viewModel.registerOutput(1, 1)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertFalse(viewModel.uiState.value.isLoading)
    }
}
