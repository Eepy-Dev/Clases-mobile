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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductViewModelTest {

    private lateinit var repository: ProductRepository
    private lateinit var viewModel: ProductViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = ProductViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadProducts updates state with products on success`() = runTest {
        // Given
        val products = listOf(Product(1, "Test", 100.0, 10))
        coEvery { repository.getProducts() } returns Result.success(products)
        coEvery { repository.getRandomDogImage() } returns Result.success("url")

        // When
        viewModel.loadProducts()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(products, viewModel.uiState.value.products)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun `addProduct updates state on success`() = runTest {
        // Given
        val newProduct = Product(null, "New", 200.0, 5)
        coEvery { repository.createProduct(any()) } returns Result.success(newProduct)
        coEvery { repository.getProducts() } returns Result.success(listOf(newProduct))
        coEvery { repository.getRandomDogImage() } returns Result.success("url")

        // When
        viewModel.addProduct("New", 200.0, 5, null)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        // Verify loadProducts was called and state updated
        assertEquals(listOf(newProduct), viewModel.uiState.value.products)
    }
}
