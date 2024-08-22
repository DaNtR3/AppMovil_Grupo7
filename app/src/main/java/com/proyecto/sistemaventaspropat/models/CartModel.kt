package com.proyecto.sistemaventaspropat.models

data class CartItem(
    val id: Int,
    val name: String,
    val base64Image: String?,
    val costWithIVA: Double,
    val quantity: Int
) {
    val totalCost: Double
        get() = costWithIVA * quantity
}
