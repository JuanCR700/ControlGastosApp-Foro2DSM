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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.tuapp.gastos.R
import com.tuapp.gastos.components.AppButton
import com.tuapp.gastos.components.AppTextField
import com.tuapp.gastos.components.AuthCard
import com.tuapp.gastos.components.ScreenContainer
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onGoToRegister: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = CredentialManager.create(context)
    val webClientId = stringResource(id = R.string.default_web_client_id)

    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }

    ScreenContainer {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = androidx.compose.ui.Modifier.weight(1f))

            Text(
                text = "Control de Gastos",
                fontSize = 30.sp
            )

            Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))

            Text(
                text = "Administra tus gastos personales de forma sencilla"
            )

            Spacer(modifier = androidx.compose.ui.Modifier.height(24.dp))

            AuthCard {
                Text(
                    text = "Iniciar sesión",
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

                Spacer(modifier = androidx.compose.ui.Modifier.height(20.dp))

                AppButton(
                    text = if (cargando) "Ingresando..." else "Iniciar sesión",
                    enabled = !cargando,
                    onClick = {
                        if (correo.isBlank() || contrasena.isBlank()) {
                            Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                            return@AppButton
                        }

                        cargando = true

                        auth.signInWithEmailAndPassword(correo, contrasena)
                            .addOnSuccessListener {
                                cargando = false
                                Toast.makeText(context, "Bienvenido", Toast.LENGTH_SHORT).show()
                                onLoginSuccess()
                            }
                            .addOnFailureListener {
                                cargando = false
                                Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                )

                Spacer(modifier = androidx.compose.ui.Modifier.height(12.dp))

                AppButton(
                    text = "Continuar con Google",
                    onClick = {
                        coroutineScope.launch {
                            try {
                                val googleIdOption = GetGoogleIdOption.Builder()
                                    .setFilterByAuthorizedAccounts(false)
                                    .setServerClientId(webClientId)
                                    .build()

                                val request = GetCredentialRequest.Builder()
                                    .addCredentialOption(googleIdOption)
                                    .build()

                                val result = credentialManager.getCredential(
                                    context = context,
                                    request = request
                                )

                                val credential = result.credential

                                if (credential is CustomCredential) {
                                    val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)

                                    val firebaseCredential = GoogleAuthProvider.getCredential(
                                        googleCredential.idToken,
                                        null
                                    )

                                    auth.signInWithCredential(firebaseCredential)
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Sesión iniciada con Google", Toast.LENGTH_SHORT).show()
                                            onLoginSuccess()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(context, "Error con Google: ${it.message}", Toast.LENGTH_LONG).show()
                                        }
                                }
                            } catch (e: NoCredentialException) {
                                Toast.makeText(context, "No se encontró una cuenta de Google", Toast.LENGTH_LONG).show()
                            } catch (e: GetCredentialException) {
                                Toast.makeText(context, "Inicio con Google cancelado o fallido", Toast.LENGTH_LONG).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                )

                Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))

                TextButton(
                    onClick = onGoToRegister
                ) {
                    Text("Crear una cuenta")
                }
            }

            Spacer(modifier = androidx.compose.ui.Modifier.weight(1f))
        }
    }
}