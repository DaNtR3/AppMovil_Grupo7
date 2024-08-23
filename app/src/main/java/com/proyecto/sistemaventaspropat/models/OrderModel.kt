// Order.kt
package com.proyecto.sistemaventaspropat.models

data class Order(
    val userId: Int,
    val firstname: String,
    val lastname: String,
    val email: String,
    val address: String,
    val phone: String,
    val pedidoId: Int,
    val fechaPedido: String,
    val estado: String,
    val total: Double,
    val fechaEnvio: String,
    val metodoEnvio: String,
    val numeroGuia: String,
    val estadoEnvio: String,
    val direccionEnvio: String,
    val productos: List<OrderProduct>
)

data class OrderProduct(
    val id: Int,
    val quantity: Int,
    val pricePerUnit: Double,
    val total: Double
)