package com.tuapp.gastos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.tuapp.gastos.navigation.AppNavigation
import com.tuapp.gastos.ui.theme.ControlGastosAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ControlGastosAppTheme {
                AppNavigation()
            }
        }
    }
}