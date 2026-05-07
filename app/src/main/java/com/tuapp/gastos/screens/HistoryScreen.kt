package com.tuapp.gastos.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tuapp.gastos.components.AppButton
import com.tuapp.gastos.components.ScreenContainer
import com.tuapp.gastos.models.Gasto

@Composable
fun HistoryScreen(
    onBack: () -> Unit
) {

    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    val gastos = remember {
        mutableStateListOf<Gasto>()
    }

    val totalMensual = remember {
        mutableDoubleStateOf(0.0)
    }

    LaunchedEffect(Unit) {

        val userId = auth.currentUser?.uid ?: ""

        db.collection("gastos")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->

                gastos.clear()

                var total = 0.0

                for (document in result.documents) {

                    val gasto = Gasto(
                        id = document.id,
                        nombre = document.getString("nombre") ?: "",
                        categoria = document.getString("categoria") ?: "",
                        fecha = document.getString("fecha") ?: "",
                        monto = document.getDouble("monto") ?: 0.0,
                        userId = document.getString("userId") ?: ""
                    )

                    gastos.add(gasto)

                    total += gasto.monto
                }

                totalMensual.doubleValue = total
            }
    }

    ScreenContainer {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Historial de gastos",
                fontSize = 30.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = "Total acumulado",
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "$ ${totalMensual.doubleValue}",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 32.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {

                items(gastos) { gasto ->

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 4.dp
                        )
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = gasto.nombre,
                                fontSize = 22.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Categoría: ${gasto.categoria}"
                            )

                            Text(
                                text = "Fecha: ${gasto.fecha}"
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "$ ${gasto.monto}",
                                fontSize = 24.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            AppButton(
                text = "Volver",
                onClick = onBack
            )
        }
    }
}