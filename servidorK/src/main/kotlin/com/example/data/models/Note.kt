package com.example.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Note(
    var id: Int,
    var usuarioMateriaId: Int,
    var apunteTitulo: String,
    var apunteTexto: String,
    var recordatorio: String,
    var fechaCreacion: String,
)