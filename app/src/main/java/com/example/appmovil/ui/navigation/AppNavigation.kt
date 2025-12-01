package com.example.appmovil.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appmovil.ui.screens.*
import com.example.appmovil.ui.viewmodel.ProductViewModel
import com.example.appmovil.ui.viewmodel.LoginViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Menu : Screen("menu")
    object Ingreso : Screen("ingreso")
    object Consulta : Screen("consulta")
    object Salida : Screen("salida")
    object Register : Screen("register")
    object Catalog : Screen("catalog")
    object History : Screen("history")
}



@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    productViewModel: ProductViewModel,
    loginViewModel: LoginViewModel = viewModel()
) {
    NavHost(
        navController = navController, 
        startDestination = Screen.Login.route,
        enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) }
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Menu.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                viewModel = loginViewModel
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.popBackStack() // Go back to login after success
                },
                onNavigateBack = { navController.popBackStack() },
                viewModel = loginViewModel
            )
        }
        composable(Screen.Menu.route) {
            MenuScreen(
                onNavigateToIngreso = { navController.navigate(Screen.Ingreso.route) },
                onNavigateToConsulta = { navController.navigate(Screen.Consulta.route) },
                onNavigateToSalida = { navController.navigate(Screen.Salida.route) },
                onNavigateToCatalog = { navController.navigate(Screen.Catalog.route) },
                onNavigateToHistory = { navController.navigate(Screen.History.route) },
                viewModel = productViewModel
            )
        }
        composable(Screen.Ingreso.route) {
            IngresoScreen(
                onNavigateBack = { navController.popBackStack() },
                viewModel = productViewModel
            )
        }
        composable(Screen.Consulta.route) {
            ConsultaScreen(
                onNavigateBack = { navController.popBackStack() },
                viewModel = productViewModel
            )
        }
        composable(Screen.Salida.route) {
            SalidaScreen(
                onNavigateBack = { navController.popBackStack() },
                viewModel = productViewModel
            )
        }
        composable(Screen.Catalog.route) {
            CatalogScreen(
                onNavigateBack = { navController.popBackStack() },
                viewModel = productViewModel
            )
        }
        composable(Screen.History.route) {
            HistoryScreen(
                onNavigateBack = { navController.popBackStack() },
                viewModel = productViewModel
            )
        }
    }
}

