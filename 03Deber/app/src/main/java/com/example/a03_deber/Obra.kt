package com.example.a03_deber

class Obra(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val artistaId: Int // Clave foránea que relaciona la obra con el artista
) {
}