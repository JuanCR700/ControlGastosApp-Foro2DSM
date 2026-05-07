package com.tuapp.gastos.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tuapp.gastos.components.AppButton
import com.tuapp.gastos.components.ScreenContainer
import com.tuapp.gastos.ui.theme.AppSecondary
import com.tuapp.gastos.ui.theme.AppTextSecondary

@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onGoToAddExpense: () -> Unit,
    onGoToHistory: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    val user = auth.currentUser
    val correo = user?.email ?: "Usuario"

    val totalMensual = remember { mutableDoubleStateOf(0.0) }
    val cantidadGastos = remember { mutableIntStateOf(0) }
    val cantidadCategorias = remember { mutableIntStateOf(0) }
    val cargando = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid ?: ""

        db.collection("gastos")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                var total = 0.0
                val categorias = mutableSetOf<String>()

                for (document in result.documents) {
                    val monto = document.getDouble("monto") ?: 0.0
                    val categoria = document.getString("categoria") ?: ""

                    total += monto

                    if (categoria.isNotBlank()) {
                        categorias.add(categoria)
                    }
                }

                totalMensual.doubleValue = total
                cantidadGastos.intValue = result.size()
                cantidadCategorias.intValue = categorias.size
                cargando.value = false
            }
            .addOnFailureListener {
                cargando.value = false
            }
    }

    ScreenContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Hola,",
                fontSize = 20.sp,
                color = AppTextSecondary
            )

            Text(
                text = correo,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Total mensual",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (cargando.value) "Cargando..." else "$ ${String.format("%.2f", totalMensual.doubleValue)}",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 36.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Resumen de tus gastos registrados",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(110.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Gastos",
                            color = AppTextSecondary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (cargando.value) "..." else cantidadGastos.intValue.toString(),
                            fontSize = 28.sp
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(110.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = AppSecondary
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Categorías",
                            color = MaterialTheme.colorScheme.onSecondary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (cargando.value) "..." else cantidadCategorias.intValue.toString(),
                            fontSize = 28.sp,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Acciones rápidas",
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            AppButton(
                text = "Agregar nuevo gasto",
                onClick = onGoToAddExpense
            )

            Spacer(modifier = Modifier.height(12.dp))

            AppButton(
                text = "Ver historial de gastos",
                onClick = onGoToHistory
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar sesión")
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}