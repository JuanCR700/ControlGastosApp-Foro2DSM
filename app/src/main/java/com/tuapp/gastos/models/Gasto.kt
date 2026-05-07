package com.tuapp.gastos.models

data class Gasto(
    val id: String = "",
    val nombre: String = "",
    val monto: Double = 0.0,
    val categoria: String = "",
    val fecha: String = "",
    val userId: String = ""
)