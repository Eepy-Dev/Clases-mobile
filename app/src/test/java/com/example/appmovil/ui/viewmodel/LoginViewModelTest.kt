package com.example.appmovil.ui.viewmodel

import com.example.appmovil.data.repository.ProductRepository
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel
    private lateinit var repository: ProductRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = LoginViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login success updates uiState to success`() = runTest(testDispatcher) {
        // Given
        coEvery { repository.login("user", "pass") } returns Result.success("token")

        // When
        viewModel.login("user", "pass")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.isSuccess)
    }

    @Test
    fun `login failure updates uiState with error`() = runTest(testDispatcher) {
        // Given
        coEvery { repository.login("user", "wrong") } returns Result.failure(Exception("Invalid credentials"))

        // When
        viewModel.login("user", "wrong")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals("Invalid credentials", viewModel.uiState.value.error)
    }
}
