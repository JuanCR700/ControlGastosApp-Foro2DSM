package com.tuapp.gastos.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.tuapp.gastos.R
import com.tuapp.gastos.components.AppButton
import com.tuapp.gastos.components.AppTextField
import com.tuapp.gastos.components.AuthCard
import com.tuapp.gastos.components.ScreenContainer
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onGoToRegister: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }

    // Configurar Google Sign-In
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    // Launcher para el intent de Google
    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                cargando = true
                auth.signInWithCredential(credential)
                    .addOnSuccessListener {
                        cargando = false
                        onLoginSuccess()
                    }
                    .addOnFailureListener {
                        cargando = false
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Error con Google: ${it.message}")
                        }
                    }
            } catch (e: ApiException) {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Error: ${e.message}")
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Control de Gastos", style = MaterialTheme.typography.titleLarge) },
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
                    text = "Administra tus gastos personales de forma sencilla",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(24.dp))

                AuthCard {
                    Text(
                        text = "Iniciar sesión",
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

                    Spacer(modifier = Modifier.height(20.dp))

                    AppButton(
                        text = if (cargando) "Ingresando..." else "Iniciar sesión",
                        enabled = !cargando,
                        onClick = {
                            if (correo.isBlank() || contrasena.isBlank()) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Completa todos los campos")
                                }
                                return@AppButton
                            }
                            cargando = true
                            auth.signInWithEmailAndPassword(correo, contrasena)
                                .addOnSuccessListener {
                                    cargando = false
                                    onLoginSuccess()
                                }
                                .addOnFailureListener {
                                    cargando = false
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Error: ${it.message}")
                                    }
                                }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            googleSignInClient.signOut().addOnCompleteListener {
                                googleLauncher.launch(googleSignInClient.signInIntent)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !cargando
                    ) {
                        Text("Continuar con Google")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = onGoToRegister) {
                        Text("Crear una cuenta", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}