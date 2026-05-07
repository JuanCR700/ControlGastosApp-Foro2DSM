package com.tuapp.gastos.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
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
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.tuapp.gastos.R

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onGoToRegister: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = CredentialManager.create(context)

    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Iniciar sesión",
            fontSize = 28.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    try {
                        val googleIdOption = GetGoogleIdOption.Builder()
                            .setFilterByAuthorizedAccounts(false)
                            .setServerClientId(context.getString(R.string.default_web_client_id))
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
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continuar con Google")
        }

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (correo.isBlank() || contrasena.isBlank()) {
                    Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                    return@Button
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
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !cargando
        ) {
            Text(if (cargando) "Ingresando..." else "Iniciar sesión")
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = onGoToRegister
        ) {
            Text("Crear una cuenta")
        }
    }
}