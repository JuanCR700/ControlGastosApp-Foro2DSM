package com.tuapp.gastos.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.tuapp.gastos.screens.HomeScreen
import com.tuapp.gastos.screens.LoginScreen
import com.tuapp.gastos.screens.RegisterScreen
import com.tuapp.gastos.screens.AddExpenseScreen
import com.tuapp.gastos.screens.HistoryScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()

    val startDestination = if (auth.currentUser != null) {
        Routes.HOME
    } else {
        Routes.LOGIN
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) {
                            inclusive = true
                        }
                    }
                },
                onGoToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.REGISTER) {
                            inclusive = true
                        }
                    }
                },
                onGoToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onLogout = {
                    auth.signOut()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) {
                            inclusive = true
                        }
                    }
                },
                onGoToAddExpense = {
                    navController.navigate(Routes.ADD_EXPENSE)
                },
                onGoToHistory = {
                    navController.navigate(Routes.HISTORY)
                }
            )
        }

        composable(Routes.ADD_EXPENSE) {
            AddExpenseScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.HISTORY) {
            HistoryScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}