package com.tuapp.gastos.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.tuapp.gastos.components.AppButton
import com.tuapp.gastos.components.AppTextField
import com.tuapp.gastos.components.AuthCard
import com.tuapp.gastos.components.ScreenContainer
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onGoToLogin: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Registro", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        ScreenContainer {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Regístrate para comenzar a controlar tus gastos",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(24.dp))

                AuthCard {
                    Text(
                        text = "Crear cuenta",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AppTextField(
                        value = correo,
                        onValueChange = { correo = it },
                        label = "Correo electrónico"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    AppTextField(
                        value = contrasena,
                        onValueChange = { contrasena = it },
                        label = "Contraseña",
                        visualTransformation = PasswordVisualTransformation()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    AppTextField(
                        value = confirmarContrasena,
                        onValueChange = { confirmarContrasena = it },
                        label = "Confirmar contraseña",
                        visualTransformation = PasswordVisualTransformation()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    AppButton(
                        text = if (cargando) "Creando cuenta..." else "Registrarse",
                        enabled = !cargando,
                        onClick = {
                            if (correo.isBlank() || contrasena.isBlank() || confirmarContrasena.isBlank()) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Completa todos los campos")
                                }
                                return@AppButton
                            }

                            if (contrasena.length < 6) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("La contraseña debe tener mínimo 6 caracteres")
                                }
                                return@AppButton
                            }

                            if (contrasena != confirmarContrasena) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Las contraseñas no coinciden")
                                }
                                return@AppButton
                            }

                            cargando = true
                            auth.createUserWithEmailAndPassword(correo, contrasena)
                                .addOnSuccessListener {
                                    cargando = false
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Cuenta creada correctamente")
                                    }
                                    onRegisterSuccess()
                                }
                                .addOnFailureListener {
                                    cargando = false
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Error: ${it.message}")
                                    }
                                }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = onGoToLogin) {
                        Text("Ya tengo cuenta", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}
