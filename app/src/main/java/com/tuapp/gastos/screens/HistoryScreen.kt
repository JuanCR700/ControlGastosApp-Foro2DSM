package com.tuapp.gastos.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tuapp.gastos.models.Gasto
import com.tuapp.gastos.ui.theme.AppTextSecondary
import com.tuapp.gastos.ui.theme.CatComida
import com.tuapp.gastos.ui.theme.CatCompras
import com.tuapp.gastos.ui.theme.CatEducacion
import com.tuapp.gastos.ui.theme.CatEntretenimiento
import com.tuapp.gastos.ui.theme.CatOtros
import com.tuapp.gastos.ui.theme.CatSalud
import com.tuapp.gastos.ui.theme.CatServicios
import com.tuapp.gastos.ui.theme.CatTransporte

fun colorParaCategoria(categoria: String): Color = when (categoria) {
    "Comida"          -> CatComida
    "Transporte"      -> CatTransporte
    "Compras"         -> CatCompras
    "Servicios"       -> CatServicios
    "Entretenimiento" -> CatEntretenimiento
    "Salud"           -> CatSalud
    "Educación"       -> CatEducacion
    else              -> CatOtros
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBack: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    val gastos = remember { mutableStateListOf<Gasto>() }
    val totalMensual = remember { mutableDoubleStateOf(0.0) }
    val cargando = remember { mutableStateOf(true) }

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
                cargando.value = false
            }
            .addOnFailureListener { cargando.value = false }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de gastos", style = MaterialTheme.typography.titleLarge) },
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Tarjeta total
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Total acumulado",
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (cargando.value) "Cargando..."
                        else "$ ${String.format("%.2f", totalMensual.doubleValue)}",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${gastos.size} gasto(s) registrado(s)",
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Cargando
            if (cargando.value) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            // Estado vacío
            if (!cargando.value && gastos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = AppTextSecondary.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No hay gastos registrados", color = AppTextSecondary, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "Agrega tu primer gasto desde la pantalla principal",
                            color = AppTextSecondary.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }


            if (!cargando.value && gastos.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    items(gastos) { gasto -> GastoItem(gasto = gasto) }
                }
            }
        }
    }
}

@Composable
fun GastoItem(gasto: Gasto) {
    val colorCategoria = colorParaCategoria(gasto.categoria)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape)
                    .background(colorCategoria.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Text(gasto.categoria.take(1), color = colorCategoria, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(gasto.nombre, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(3.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CategoryChip(label = gasto.categoria, color = colorCategoria)
                    Text(gasto.fecha, style = MaterialTheme.typography.bodySmall, color = AppTextSecondary)
                }
            }
            Text(
                "$ ${String.format("%.2f", gasto.monto)}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun CategoryChip(label: String, color: Color) {
    Box(
        modifier = Modifier.clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(label, color = color, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}