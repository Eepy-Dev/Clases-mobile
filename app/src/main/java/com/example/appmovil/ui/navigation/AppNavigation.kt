package com.example.appmovil.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appmovil.ui.screens.*
import com.example.appmovil.ui.viewmodel.ProductViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Menu : Screen("menu")
    object Ingreso : Screen("ingreso")
    object Consulta : Screen("consulta")
    object Salida : Screen("salida")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    viewModel: ProductViewModel = viewModel()
) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(onLoginSuccess = {
                navController.navigate(Screen.Menu.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Menu.route) {
            MenuScreen(
                onNavigateToIngreso = { navController.navigate(Screen.Ingreso.route) },
                onNavigateToConsulta = { navController.navigate(Screen.Consulta.route) },
                onNavigateToSalida = { navController.navigate(Screen.Salida.route) },
                viewModel = viewModel
            )
        }
        composable(Screen.Ingreso.route) {
            IngresoScreen(
                onNavigateBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
        composable(Screen.Consulta.route) {
            ConsultaScreen(
                onNavigateBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
        composable(Screen.Salida.route) {
            SalidaScreen(
                onNavigateBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
    }
}
