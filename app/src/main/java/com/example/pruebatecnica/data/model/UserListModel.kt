package com.example.pruebatecnica.data.model

data class UserListModel(
    val id: Int,
    val nombre: String,
    val apellidoPaterno: String,
    val apellidoMaterno: String,
    val edad: Int,
    val email: String,
    val fechaNac: String,
    var datos: List<Datos>
)