package com.tuapp.gastos.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        "Comida",
        "Transporte",
        "Compras",
        "Servicios",
        "Entretenimiento",
        "Salud",
        "Educación",
        "Otros"
    )

    ScreenContainer {

        Column {

            Spacer(modifier = androidx.compose.ui.Modifier.height(24.dp))

            Text(
                text = "Nuevo gasto",
                fontSize = 30.sp
            )

            Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))

            Text(
                text = "Registra un nuevo movimiento"
            )

            Spacer(modifier = androidx.compose.ui.Modifier.height(24.dp))

            AuthCard {

                AppTextField(
                    value = nombre,
                    onValueChange = {
                        nombre = it
                    },
                    label = "Nombre del gasto"
                )

                Spacer(modifier = androidx.compose.ui.Modifier.height(12.dp))

                OutlinedTextField(
                    value = monto,
                    onValueChange = {
                        monto = it
                    },
                    label = {
                        Text("Monto")
                    },
                    modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    singleLine = true
                )

                Spacer(modifier = androidx.compose.ui.Modifier.height(12.dp))

                Column {

                    OutlinedButton(
                        onClick = {
                            expanded = true
                        },
                        modifier = androidx.compose.ui.Modifier.fillMaxWidth()
                    ) {

                        Text(
                            text = if (categoria.isBlank())
                                "Seleccionar categoría"
                            else
                                categoria
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false
                        }
                    ) {

                        categorias.forEach { item ->

                            DropdownMenuItem(
                                text = {
                                    Text(item)
                                },
                                onClick = {
                                    categoria = item
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = androidx.compose.ui.Modifier.height(20.dp))

                AppButton(
                    text = "Guardar gasto",
                    onClick = {

                        if (
                            nombre.isBlank() ||
                            monto.isBlank() ||
                            categoria.isBlank()
                        ) {

                            Toast.makeText(
                                context,
                                "Completa todos los campos",
                                Toast.LENGTH_SHORT
                            ).show()

                            return@AppButton
                        }

                        val montoDouble = monto.toDoubleOrNull()

                        if (montoDouble == null || montoDouble <= 0) {

                            Toast.makeText(
                                context,
                                "Ingresa un monto válido",
                                Toast.LENGTH_SHORT
                            ).show()

                            return@AppButton
                        }

                        val userId = auth.currentUser?.uid ?: ""

                        val fechaActual = SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                        ).format(Date())

                        val gasto = hashMapOf(
                            "nombre" to nombre,
                            "monto" to montoDouble,
                            "categoria" to categoria,
                            "fecha" to fechaActual,
                            "userId" to userId
                        )

                        db.collection("gastos")
                            .add(gasto)
                            .addOnSuccessListener {

                                Toast.makeText(
                                    context,
                                    "Gasto guardado",
                                    Toast.LENGTH_SHORT
                                ).show()

                                onBack()
                            }
                            .addOnFailureListener {

                                Toast.makeText(
                                    context,
                                    "Error al guardar",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                )

                Spacer(modifier = androidx.compose.ui.Modifier.height(12.dp))

                AppButton(
                    text = "Volver",
                    onClick = onBack
                )
            }
        }
    }
}