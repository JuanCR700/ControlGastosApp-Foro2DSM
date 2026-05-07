package com.tuapp.gastos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import com.tuapp.gastos.screens.LoginScreen
import com.tuapp.gastos.screens.RegisterScreen
import com.tuapp.gastos.ui.theme.ControlGastosAppTheme
import com.tuapp.gastos.screens.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = FirebaseAuth.getInstance()

        setContent {
            ControlGastosAppTheme {
                var pantallaActual by remember {
                    mutableStateOf(
                        if (auth.currentUser != null) "home" else "login"
                    )
                }

                when (pantallaActual) {
                    "login" -> LoginScreen(
                        onLoginSuccess = {
                            pantallaActual = "home"
                        },
                        onGoToRegister = {
                            pantallaActual = "register"
                        }
                    )

                    "register" -> RegisterScreen(
                        onRegisterSuccess = {
                            pantallaActual = "home"
                        },
                        onGoToLogin = {
                            pantallaActual = "login"
                        }
                    )

                    "home" -> HomeScreen(
                        onLogout = {
                            auth.signOut()
                            pantallaActual = "login"
                        }
                    )
                }
            }
        }
    }
}