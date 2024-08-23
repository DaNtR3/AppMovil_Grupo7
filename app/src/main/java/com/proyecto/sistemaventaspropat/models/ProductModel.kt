package com.proyecto.sistemaventaspropat.models

data class Product(
    val id: Int,
    val name: String,
    val base64Image: String?,
    val costWithIva: Double,
    val costWithoutIva: Double,
    val amount: Int,
    val color: String
)