package com.tuapp.gastos.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.tuapp.gastos.components.AppButton
import com.tuapp.gastos.components.AppTextField
import com.tuapp.gastos.components.AuthCard
import com.tuapp.gastos.components.ScreenContainer

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onGoToLogin: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }

    ScreenContainer {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = androidx.compose.ui.Modifier.weight(1f))

            Text(
                text = "Crear cuenta",
                fontSize = 30.sp
            )

            Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))

            Text(
                text = "Regístrate para comenzar a controlar tus gastos"
            )

            Spacer(modifier = androidx.compose.ui.Modifier.height(24.dp))

            AuthCard {
                Text(
                    text = "Registro",
                    fontSize = 24.sp
                )

                Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))

                AppTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = "Correo electrónico"
                )

                Spacer(modifier = androidx.compose.ui.Modifier.height(12.dp))

                AppTextField(
                    value = contrasena,
                    onValueChange = { contrasena = it },
                    label = "Contraseña",
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(modifier = androidx.compose.ui.Modifier.height(12.dp))

                AppTextField(
                    value = confirmarContrasena,
                    onValueChange = { confirmarContrasena = it },
                    label = "Confirmar contraseña",
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(modifier = androidx.compose.ui.Modifier.height(20.dp))

                AppButton(
                    text = if (cargando) "Creando cuenta..." else "Registrarse",
                    enabled = !cargando,
                    onClick = {
                        if (
                            correo.isBlank() ||
                            contrasena.isBlank() ||
                            confirmarContrasena.isBlank()
                        ) {
                            Toast.makeText(
                                context,
                                "Completa todos los campos",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@AppButton
                        }

                        if (contrasena.length < 6) {
                            Toast.makeText(
                                context,
                                "La contraseña debe tener mínimo 6 caracteres",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@AppButton
                        }

                        if (contrasena != confirmarContrasena) {
                            Toast.makeText(
                                context,
                                "Las contraseñas no coinciden",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@AppButton
                        }

                        cargando = true

                        auth.createUserWithEmailAndPassword(correo, contrasena)
                            .addOnSuccessListener {
                                cargando = false
                                Toast.makeText(
                                    context,
                                    "Cuenta creada correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onRegisterSuccess()
                            }
                            .addOnFailureListener {
                                cargando = false
                                Toast.makeText(
                                    context,
                                    "Error: ${it.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    }
                )

                Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))

                TextButton(
                    onClick = onGoToLogin
                ) {
                    Text("Ya tengo cuenta")
                }
            }

            Spacer(modifier = androidx.compose.ui.Modifier.weight(1f))
        }
    }
}