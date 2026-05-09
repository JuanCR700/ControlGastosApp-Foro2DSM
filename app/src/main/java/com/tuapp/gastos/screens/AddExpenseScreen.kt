package com.tuapp.gastos.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tuapp.gastos.components.AppButton
import com.tuapp.gastos.components.AppTextField
import com.tuapp.gastos.components.AuthCard
import com.tuapp.gastos.components.ScreenContainer
import com.tuapp.gastos.ui.theme.AppTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    var nombre by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val categorias = listOf(
        "Comida", "Transporte", "Compras", "Servicios",
        "Entretenimiento", "Salud", "Educación", "Otros"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo gasto", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->

        ScreenContainer {
            Column(modifier = Modifier.padding(paddingValues)) {

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Registra un nuevo movimiento",
                    style = MaterialTheme.typography.bodyLarge,
                    color = AppTextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                AuthCard {
                    AppTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = "Nombre del gasto"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    AppTextField(
                        value = monto,
                        onValueChange = { monto = it },
                        label = "Monto ($)",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Box {
                        OutlinedButton(
                            onClick = { expanded = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (categoria.isBlank()) "Seleccionar categoría" else categoria,
                                modifier = Modifier.weight(1f),
                                fontSize = 15.sp
                            )
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            categorias.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item) },
                                    onClick = {
                                        categoria = item
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    AppButton(
                        text = "Guardar gasto",
                        onClick = {
                            if (nombre.isBlank() || monto.isBlank() || categoria.isBlank()) {
                                Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                                return@AppButton
                            }
                            val montoDouble = monto.toDoubleOrNull()
                            if (montoDouble == null || montoDouble <= 0) {
                                Toast.makeText(context, "Ingresa un monto válido mayor a 0", Toast.LENGTH_SHORT).show()
                                return@AppButton
                            }
                            val userId = auth.currentUser?.uid ?: ""
                            val fechaActual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                            val gasto = hashMapOf(
                                "nombre"    to nombre.trim(),
                                "monto"     to montoDouble,
                                "categoria" to categoria,
                                "fecha"     to fechaActual,
                                "userId"    to userId
                            )
                            db.collection("gastos").add(gasto)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Gasto guardado exitosamente ✓", Toast.LENGTH_SHORT).show()
                                    onBack()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Error al guardar: ${it.message}", Toast.LENGTH_LONG).show()
                                }
                        }
                    )
                }
            }
        }
    }
}