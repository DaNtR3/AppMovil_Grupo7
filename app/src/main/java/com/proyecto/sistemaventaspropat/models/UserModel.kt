package com.proyecto.sistemaventaspropat.models

data class User(
    val id: Int,
    val role: Int,
    val firstname: String,
    val lastname: String,
    val email: String,
    val phone: String,
    val address: String
)
