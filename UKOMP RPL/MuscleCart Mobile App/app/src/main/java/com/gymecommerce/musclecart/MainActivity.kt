package com.gymecommerce.musclecart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gymecommerce.musclecart.navigation.NavRoutes
import com.gymecommerce.musclecart.presentation.auth.LoginScreen
import com.gymecommerce.musclecart.presentation.auth.RegisterScreen
import com.gymecommerce.musclecart.presentation.cart.CartViewModel
import com.gymecommerce.musclecart.presentation.main.MainScreen
import com.gymecommerce.musclecart.presentation.splash.SplashScreen
import com.gymecommerce.musclecart.ui.theme.MuscleCartTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MuscleCartTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MuscleCartApp()
                }
            }
        }
    }
}

@Composable
fun MuscleCartApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.SPLASH
    ) {
        composable(NavRoutes.SPLASH) {
            SplashScreen(
                onNavigateToAuth = {
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(NavRoutes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToMain = {
                    navController.navigate(NavRoutes.MAIN) {
                        popUpTo(NavRoutes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.LOGIN) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(NavRoutes.REGISTER)
                },
                onLoginSuccess = {
                    navController.navigate(NavRoutes.MAIN) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.REGISTER) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                },
                onRegisterSuccess = {
                    navController.navigate(NavRoutes.MAIN) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.MAIN) {
            val cartViewModel: CartViewModel = hiltViewModel()
            MainScreen(
                onLogout = {
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(NavRoutes.MAIN) { inclusive = true }
                    }
                },
                cartViewModel = cartViewModel
            )
        }
    }
}
